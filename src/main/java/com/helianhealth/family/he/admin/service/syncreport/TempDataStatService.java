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
 * temp 目录数据统计工具：
 *   将 temp 下所有解压子目录中的 JSON 全部加载到内存，
 *   统计总记录数，并按身份证号（shenfenZh）全局去重后输出唯一人数。
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

        // 第一步：收集 temp 下所有 .json 文件路径
        List<Path> allJsonFiles = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(tempRoot)) {
            walk.filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".json"))
                .forEach(allJsonFiles::add);
        } catch (IOException e) {
            log.error("[统计] 遍历 temp 目录失败", e);
            return;
        }
        log.info("[统计] 共发现 JSON 文件: {} 个，开始全量加载到内存...", allJsonFiles.size());

        // 第二步：将所有记录全部加载到内存
        List<GwtjRecord> allRecords = new ArrayList<>();
        int parseFailFiles = 0;
        for (Path jsonFile : allJsonFiles) {
            try {
                String content = new String(Files.readAllBytes(jsonFile), StandardCharsets.UTF_8);
                List<GwtjRecord> records = GSON.fromJson(content,
                        new TypeToken<List<GwtjRecord>>() {}.getType());
                if (records != null) {
                    allRecords.addAll(records);
                    log.info("[统计] 加载 {} -> {} 条", jsonFile.getFileName(), records.size());
                }
            } catch (Exception e) {
                parseFailFiles++;
                log.warn("[统计] JSON 解析失败: {}", jsonFile.toAbsolutePath(), e);
            }
        }
        log.info("[统计] 全部记录已加载完毕，总条数={}", allRecords.size());

        // 第三步：全局按 shenfenZh 去重
        Set<String> uniqueIds   = new HashSet<>();
        List<String> emptyIdNames = new ArrayList<>();  // 身份证为空的条目

        for (GwtjRecord record : allRecords) {
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

        // 输出汇总
        log.info("========== temp 数据统计汇总 ==========");
        log.info("扫描 JSON 文件数 : {}", allJsonFiles.size());
        log.info("解析失败文件数  : {}", parseFailFiles);
        log.info("内存中总记录数   : {}", allRecords.size());
        log.info("全局唯一人数    : {} （按身份证号去重）", uniqueIds.size());
        if (!emptyIdNames.isEmpty()) {
            log.info("身份证为空的条目: {} 条（未纳入去重）", emptyIdNames.size());
            log.info("身份证为空姓名列表: {}", emptyIdNames);
        }
        log.info("======================================");
    }

    /** 独立运行入口 */
    public static void main(String[] args) {
        new TempDataStatService().stat();
    }
}

