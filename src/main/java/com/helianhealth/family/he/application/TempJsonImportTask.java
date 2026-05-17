package com.helianhealth.family.he.application;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.helianhealth.family.he.admin.db.MyBatisUtil;
import com.helianhealth.family.he.admin.db.entity.SyncRecord;
import com.helianhealth.family.he.admin.db.mapper.SyncRecordMapper;
import com.helianhealth.family.he.admin.model.wgtj.DanganInfo;
import com.helianhealth.family.he.admin.model.wgtj.GwtjRecord;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * temp 目录 JSON 流式导入任务（大数据量防 OOM 版）。
 * <p>
 * 架构：
 * <pre>
 *   解析线程池（N 线程）
 *     → JsonReader 逐条流式解析（每次内存 = 1 条 GwtjRecord）
 *     → 放入有界 BlockingQueue（队列满则自动阻塞，实现背压）
 *     → 主线程（单 DB 写）逐条取出，每 COMMIT_BATCH 条提交一次事务
 * </pre>
 * 峰值内存 ≈ 解析线程数 × 1条记录 + 队列容量 × 1条记录，与文件总量无关。
 */
public class TempJsonImportTask {

    private static final Logger log = LoggerFactory.getLogger(TempJsonImportTask.class);

    /** Gson 线程安全，多线程共享 */
    private static final Gson GSON = new Gson();

    private static final String TEMP_DIR = "./temp";

    /** 解析线程数：取 CPU 核数与 4 的较小值，至少 2 */
    private static final int PARSE_THREADS =
            Math.max(2, Math.min(4, Runtime.getRuntime().availableProcessors()));

    /** 解析 → 写库之间的缓冲队列容量（条），满则解析线程阻塞 */
    private static final int QUEUE_CAPACITY = 500;

    /** 每写入多少条提交一次事务（SQLite 批量提交性能关键） */
    private static final int COMMIT_BATCH = 200;

    /** 哨兵：通知 DB 写线程全部解析已完成 */
    private static final SyncRecord SENTINEL = new SyncRecord();

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

        // ── 收集所有 (file, hospitalFid, month) 任务 ───────────────────────
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
        System.out.printf("解析线程数：%d  待处理文件数：%d  队列上限：%d 条%n",
                PARSE_THREADS, tasks.size(), QUEUE_CAPACITY);

        // ── 有界队列：满了解析线程自动阻塞，控制内存上限 ────────────────────
        BlockingQueue<SyncRecord> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        AtomicInteger errorFiles = new AtomicInteger(0);

        // ── 提交所有解析任务到线程池 ─────────────────────────────────────────
        ExecutorService pool = Executors.newFixedThreadPool(PARSE_THREADS);
        for (Object[] task : tasks) {
            final File   file  = (File)   task[0];
            final String fid   = (String) task[1];
            final String month = (String) task[2];
            pool.submit(() -> {
                try {
                    streamParseFile(file, fid, month, queue);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("[TempJsonImportTask] 解析线程被中断：{}", file.getName());
                } catch (Exception e) {
                    errorFiles.incrementAndGet();
                    log.error("[TempJsonImportTask] 解析失败：{}", file.getAbsolutePath(), e);
                    System.out.printf("  [错误] %s  原因：%s%n", file.getName(), e.getMessage());
                }
            });
        }
        pool.shutdown();

        // ── 哨兵线程：所有解析任务完成后向队列投入哨兵，通知写线程退出 ────────
        Thread sentinel = new Thread(() -> {
            try {
                pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                queue.put(SENTINEL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "sentinel-thread");
        sentinel.setDaemon(true);
        sentinel.start();

        // ── 主线程：单独写库（SQLite 不支持并发写） ──────────────────────────
        int totalInserted = 0;
        int pending       = 0;

        try (SqlSession session = MyBatisUtil.openManualSession()) {
            SyncRecordMapper mapper = session.getMapper(SyncRecordMapper.class);

            SyncRecord record;
            while ((record = queue.take()) != SENTINEL) {
                mapper.insertIgnore(record);
                pending++;
                totalInserted++;

                // 每 COMMIT_BATCH 条提交一次，减少 fsync 次数
                if (pending >= COMMIT_BATCH) {
                    session.commit();
                    pending = 0;
                    System.out.printf("\r[进度] 已插入：%d 条", totalInserted);
                }
            }

            // 提交剩余不满一批的记录
            if (pending > 0) {
                session.commit();
            }
        }

        System.out.println("\n==========================================");
        System.out.printf("[汇总] 处理文件数：%d（解析失败：%d）%n", tasks.size(), errorFiles.get());
        System.out.printf("[汇总] 共插入：%d 条%n", totalInserted);
        System.out.println("========== TempJsonImportTask 完成 ==========");
    }

    /**
     * 流式解析一个 JSON 数组文件，每条记录转换后立即放入队列。
     * <p>
     * 使用 Gson {@link JsonReader} 流式 API：
     * 任意时刻内存中只有当前正在解析的 1 条 {@link GwtjRecord}，
     * 转换为 {@link SyncRecord} 后原对象即可被 GC 回收。
     */
    private void streamParseFile(File file, String hospitalFid, String month,
                                  BlockingQueue<SyncRecord> queue) throws Exception {
        try (JsonReader reader = new JsonReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            reader.beginArray();
            while (reader.hasNext()) {
                // 只反序列化当前一条，之后 GwtjRecord 对象离开作用域即可 GC
                GwtjRecord record = GSON.fromJson(reader, GwtjRecord.class);

                DanganInfo dangan = record.getDanganInfo();
                SyncRecord sr = new SyncRecord();
                sr.setHospitalFid(hospitalFid);
                sr.setMonth(month);
                sr.setXingming(dangan != null ? dangan.getXingming() : null);
                sr.setShenfenzh(dangan != null ? dangan.getShenfenZh() : null);
                sr.setTijianDate(record.getTijianInfo() != null
                        ? record.getTijianInfo().getCreateTime() : null);
                sr.setJsonContent(GSON.toJson(record));

                // 队列满时此处阻塞，自动对解析线程施加背压
                queue.put(sr);
            }
            reader.endArray();
        }
    }

    /**
     * 从文件夹名解析 hospitalFid 和 month。
     * 命名规则：{hospitalFid}_{yyyy-MM}，按最后一个 _ 分割。
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
