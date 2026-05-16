package com.helianhealth.family.he.admin.service.syncreport;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.helianhealth.family.he.admin.model.wgtj.GwtjRecord;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * temp 目录数据统计工具。
 *
 * 优化策略（避免 OOM）：
 *   - 逐文件读取并解析，解析完立即丢弃 List<GwtjRecord>，让 GC 回收
 *   - 只将 shenfenZh（18 字节字符串）放入 HashSet，内存占用极低
 *   - 全量数据几万条也不会 OOM
 */
@Slf4j
public class TempDataStatService {

    private static final Gson GSON = new Gson();

    public void stat() {
        Path tempRoot = Paths.get(System.getProperty("user.dir"), "temp");
        if (!Files.exists(tempRoot)) {
            log.info("[统计] temp 目录不存在: {}", tempRoot.toAbsolutePath());
            return;
        }

        // 收集所有 .json 文件路径（不加载内容）
        List<Path> allJsonFiles = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(tempRoot)) {
            walk.filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".json"))
                .sorted()
                .forEach(allJsonFiles::add);
        } catch (IOException e) {
            log.error("[统计] 遍历 temp 目录失败", e);
            return;
        }
        log.info("[统计] 共发现 JSON 文件: {} 个，开始逐文件处理...", allJsonFiles.size());

        // 全局去重集合：只存身份证号字符串，内存极小
        Set<String> uniqueIds     = new HashSet<>();
        List<String> emptyIdNames = new ArrayList<>();  // 身份证为空的姓名，辅助排查
        int totalRecords   = 0;
        int parseFailFiles = 0;

        for (Path jsonFile : allJsonFiles) {
            List<GwtjRecord> records = null;
            try {
                String content = new String(Files.readAllBytes(jsonFile), StandardCharsets.UTF_8);
                records = GSON.fromJson(content, new TypeToken<List<GwtjRecord>>() {}.getType());
            } catch (Exception e) {
                parseFailFiles++;
                log.warn("[统计] JSON 解析失败: {}", jsonFile.toAbsolutePath(), e);
                continue;
            }

            if (records == null || records.isEmpty()) {
                log.info("[统计] {} 解析结果为空，跳过", jsonFile.getFileName());
                continue;
            }

            int fileCount = 0;
            for (GwtjRecord record : records) {
                totalRecords++;
                fileCount++;
                String idNo = null;
                String name = null;
                if (record.getDanganInfo() != null) {
                    idNo = record.getDanganInfo().getShenfenZh();
                    name = record.getDanganInfo().getXingming();
                }
                if (idNo != null && !idNo.isBlank()) {
                    uniqueIds.add(idNo);
                } else {
                    emptyIdNames.add(name != null ? name : "unknown");
                }
            }
            // records 出了循环后无强引用，GC 可自动回收
            log.info("[统计] {} -> {} 条  |  累计 {} 条  |  去重唯一 {} 人",
                    jsonFile.getFileName(), fileCount, totalRecords, uniqueIds.size());
        }

        // 汇总输出
        log.info("========== temp 数据统计汇总 ==========");
        log.info("扫描 JSON 文件数  : {}", allJsonFiles.size());
        log.info("解析失败文件数   : {}", parseFailFiles);
        log.info("总记录条数      : {}", totalRecords);
        log.info("全局唯一人数     : {} （按身份证号去重）", uniqueIds.size());
        if (!emptyIdNames.isEmpty()) {
            log.info("身份证为空的条目  : {} 条（未纳入去重）", emptyIdNames.size());
            log.info("身份证为空姓名列表: {}", emptyIdNames);
        }
        log.info("======================================");
    }

    /** 独立运行入口 */
    public static void main(String[] args) {
        new TempDataStatService().stat();
    }
}
