package com.helianhealth.family.he.application;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.helianhealth.family.he.admin.db.MyBatisUtil;
import com.helianhealth.family.he.admin.db.entity.SyncRecord;
import com.helianhealth.family.he.admin.db.mapper.SyncRecordMapper;
import com.helianhealth.family.he.admin.model.wgtj.DanganInfo;
import com.helianhealth.family.he.admin.model.wgtj.GwtjRecord;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * temp 目录 JSON 多线程导入任务。
 * <p>
 * 架构：解析线程池（并行读文件 + 解析 JSON）→ CompletionService →
 *        主线程单独写库（SQLite 不支持并发写，避免锁冲突）。
 * <p>
 * 每个 JSON 文件对应一个解析任务，解析结果通过 CompletionService 返回给
 * 主线程，主线程批量 INSERT 后按文件提交事务。
 */
public class TempJsonImportTask {

    private static final Logger log = LoggerFactory.getLogger(TempJsonImportTask.class);

    /** Gson 实例是线程安全的，多线程共享 */
    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<GwtjRecord>>() {}.getType();

    private static final String TEMP_DIR = "./temp";

    /** 解析线程数：取 CPU 核数与 4 的较小值，至少 2 */
    private static final int PARSE_THREADS =
            Math.max(2, Math.min(4, Runtime.getRuntime().availableProcessors()));

    /** 单个文件的解析结果 */
    private static class ParseResult {
        final File file;
        final List<SyncRecord> records;
        final Exception error;

        ParseResult(File file, List<SyncRecord> records) {
            this.file = file;
            this.records = records;
            this.error = null;
        }

        ParseResult(File file, Exception error) {
            this.file = file;
            this.records = null;
            this.error = error;
        }
    }

    public void run() throws Exception {
        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists() || !tempDir.isDirectory()) {
            log.warn("[TempJsonImportTask] temp 目录不存在：{}", tempDir.getAbsolutePath());
            return;
        }

        File[] subDirs = tempDir.listFiles(File::isDirectory);
        if (subDirs == null || subDirs.length == 0) {
            log.info("[TempJsonImportTask] temp 目录下无子文件夹，退出");
            return;
        }

        // ── 第一步：收集所有待处理的 (文件, hospitalFid, month) ────────────
        // 用 Object[] 而非 Map.Entry 避免额外依赖
        List<Object[]> tasks = new ArrayList<>();
        for (File dir : subDirs) {
            String[] parsed = parseDirName(dir.getName());
            if (parsed == null) {
                log.warn("[TempJsonImportTask] 跳过不符合命名规范的目录：{}", dir.getName());
                continue;
            }
            File[] jsonFiles = dir.listFiles(f -> f.isFile() && f.getName().endsWith(".json"));
            if (jsonFiles == null || jsonFiles.length == 0) continue;

            for (File f : jsonFiles) {
                tasks.add(new Object[]{f, parsed[0], parsed[1]});
            }
        }

        if (tasks.isEmpty()) {
            System.out.println("[TempJsonImportTask] 无可处理的 JSON 文件，退出");
            return;
        }

        System.out.println("========== TempJsonImportTask 开始 ==========");
        System.out.printf("解析线程数：%d  待处理文件数：%d%n", PARSE_THREADS, tasks.size());

        // ── 第二步：多线程并行解析，结果通过 CompletionService 返回 ─────────
        ExecutorService pool = Executors.newFixedThreadPool(PARSE_THREADS);
        CompletionService<ParseResult> cs = new ExecutorCompletionService<>(pool);

        for (Object[] task : tasks) {
            final File file       = (File)   task[0];
            final String fid      = (String) task[1];
            final String month    = (String) task[2];
            cs.submit(() -> {
                try {
                    return new ParseResult(file, parseFile(file, fid, month));
                } catch (Exception e) {
                    return new ParseResult(file, e);
                }
            });
        }
        // 所有任务已提交，不再接受新任务
        pool.shutdown();

        // ── 第三步：主线程单独写库（每完成一个文件提交一次事务） ──────────
        int totalInserted = 0;
        int errorFiles    = 0;

        try (SqlSession session = MyBatisUtil.openManualSession()) {
            SyncRecordMapper mapper = session.getMapper(SyncRecordMapper.class);

            for (int i = 0; i < tasks.size(); i++) {
                // 阻塞等待下一个解析完成的文件（先完成的先写库）
                ParseResult result = cs.take().get();

                if (result.error != null) {
                    errorFiles++;
                    log.error("[TempJsonImportTask] 解析失败：{}", result.file.getAbsolutePath(), result.error);
                    System.out.printf("  [错误] %s  原因：%s%n",
                            result.file.getName(), result.error.getMessage());
                    continue;
                }

                // 批量写入本文件的所有记录，再提交
                for (SyncRecord r : result.records) {
                    mapper.insertIgnore(r);
                }
                session.commit();
                totalInserted += result.records.size();
                System.out.printf("  [写库] %-50s  插入=%d%n",
                        result.file.getName(), result.records.size());
            }
        }

        // 等待线程池完全退出（正常情况下此时已全部完成）
        pool.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\n==========================================");
        System.out.printf("[汇总] 处理文件数：%d（失败：%d）%n", tasks.size(), errorFiles);
        System.out.printf("[汇总] 共插入：%d 条%n", totalInserted);
        System.out.println("========== TempJsonImportTask 完成 ==========");
    }

    /**
     * 读取并解析一个 JSON 文件，返回 SyncRecord 列表（纯解析，不涉及 DB）。
     * 此方法在解析线程中执行，Gson 线程安全，可放心并发调用。
     */
    private List<SyncRecord> parseFile(File file, String hospitalFid, String month) throws IOException {
        String raw = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        List<GwtjRecord> records = GSON.fromJson(raw, LIST_TYPE);
        if (records == null || records.isEmpty()) return new ArrayList<>();

        List<SyncRecord> result = new ArrayList<>(records.size());
        for (GwtjRecord record : records) {
            DanganInfo dangan = record.getDanganInfo();

            SyncRecord sr = new SyncRecord();
            sr.setHospitalFid(hospitalFid);
            sr.setMonth(month);
            sr.setXingming(dangan != null ? dangan.getXingming() : null);
            sr.setShenfenzh(dangan != null ? dangan.getShenfenZh() : null);
            sr.setTijianDate(record.getTijianInfo() != null
                    ? record.getTijianInfo().getCreateTime() : null);
            sr.setJsonContent(GSON.toJson(record));
            result.add(sr);
        }
        return result;
    }

    /**
     * 从文件夹名解析 hospitalFid 和 month。
     * 命名规则：{hospitalFid}_{yyyy-MM}，按最后一个 _ 分割。
     *
     * @return [hospitalFid, month]，不符合则返回 null
     */
    private String[] parseDirName(String dirName) {
        int lastUnderscore = dirName.lastIndexOf('_');
        if (lastUnderscore < 1) return null;

        String possibleMonth = dirName.substring(lastUnderscore + 1);
        if (!possibleMonth.matches("\\d{4}-\\d{2}")) return null;

        String fid = dirName.substring(0, lastUnderscore);
        if (fid.isEmpty()) return null;

        return new String[]{fid, possibleMonth};
    }
}
