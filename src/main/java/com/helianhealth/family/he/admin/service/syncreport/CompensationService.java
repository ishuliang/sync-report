package com.helianhealth.family.he.admin.service.syncreport;

import com.helianhealth.family.he.admin.db.MyBatisUtil;
import com.helianhealth.family.he.admin.db.entity.SyncTask;
import com.helianhealth.family.he.admin.db.mapper.SyncTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据补偿服务，提供两种补偿策略：
 *   1. compensate()            - 利用 DB 已存的 taskId/zipPwd 直接下载重推（status=SUCCESS 且 total=0）
 *   2. compensateByDataPrepare() - 重新走 dataPrepare 全流程（所有 total=0 任务，不限状态）
 */
@Slf4j
public class CompensationService {

    private static final Properties CONF = loadConf();

    private static Properties loadConf() {
        Properties p = new Properties();
        try (InputStream in = CompensationService.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) p.load(in);
        } catch (IOException e) {
            // 配置加载失败，后续使用默认值
        }
        return p;
    }

    // ─────────────────────────────────────────────────────────────────────
    // 策略一：利用已存储的 taskId/zipPwd 直接下载（跳过 dataPrepare）
    // ─────────────────────────────────────────────────────────────────────

    /**
     * 执行补偿：查询 status=SUCCESS 且 total_count=0 的任务，
     * 使用多线程并行利用已存储的 taskId/zipPwd 直接下载文件并重新推送。
     */
    public void compensate() {
        List<SyncTask> tasks = selectSuccessWithZeroTotal();
        if (tasks.isEmpty()) {
            log.info("[补偿] 没有需要补偿的任务（status=SUCCESS 且 total_count=0）");
            return;
        }
        log.info("[补偿] 发现需要补偿的任务: {} 条", tasks.size());
        for (SyncTask task : tasks) {
            log.info("[补偿] hospital={}, month={}, taskId={}, id={}",
                    task.getHospitalName(), task.getMonth(), task.getTaskId(), task.getId());
        }

        String gwFid     = CONF.getProperty("gateway.fid",      "HL99999");
        String gwIsPrint = CONF.getProperty("gateway.isPrint",  "2");
        String gwUserId  = CONF.getProperty("gateway.userId",   "");
        String gwToken   = CONF.getProperty("gateway.token",    "");
        String gwStation = CONF.getProperty("gateway.stationId","");
        int threadCount  = Integer.parseInt(CONF.getProperty("compensation.threadCount", "20"));

        GwtjReportService service = new GwtjReportService();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount    = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (SyncTask task : tasks) {
            executor.submit(() -> {
                if (task.getTaskId() == null || task.getZipPwd() == null) {
                    log.warn("[补偿] 跳过：taskId 或 zipPwd 为空 id={}, hospital={}, month={}",
                            task.getId(), task.getHospitalName(), task.getMonth());
                    failCount.incrementAndGet();
                    return;
                }
                String result = service.reprocessByStoredTask(task, gwFid, gwIsPrint, gwUserId, gwToken, gwStation);
                if (result.startsWith("success")) {
                    successCount.incrementAndGet();
                } else {
                    failCount.incrementAndGet();
                }
            });
        }
        executor.shutdown();
        try {
            boolean finished = executor.awaitTermination(2, TimeUnit.HOURS);
            if (!finished) log.warn("[补偿] 部分任务超时未完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[补偿] 等待线程池结束时被中断", e);
        }
        log.info("[补偿] 全部执行完毕: 总计={}, 成功={}, 失败={}",
                tasks.size(), successCount.get(), failCount.get());
    }

    // ─────────────────────────────────────────────────────────────────────
    // 策略二：重新走 dataPrepare 全流程
    // ─────────────────────────────────────────────────────────────────────

    /**
     * 全量补偿（重走 dataPrepare 全流程）：
     * 查询所有 total_count=0 的任务（不限状态），
     * 每条重新调用 dataPrepareMonth → 轮询完成 → 下载 → 解析 → 推送。
     * 适合旧数据不存 taskId/zipPwd，或现有 taskId 已过期的情况。
     */
    public void compensateByDataPrepare() {
        List<SyncTask> tasks = selectAllWithZeroTotal();
        if (tasks.isEmpty()) {
            log.info("[补偿-dataPrepare] 没有需要补偿的任务（total_count=0）");
            return;
        }
        log.info("[补偿-dataPrepare] 发现需要补偿的任务: {} 条", tasks.size());
        for (SyncTask task : tasks) {
            log.info("[补偿-dataPrepare] hospital={}, month={}, status={}, id={}",
                    task.getHospitalName(), task.getMonth(), task.getStatus(), task.getId());
        }

        String gwFid     = CONF.getProperty("gateway.fid",       "HL99999");
        String gwIsPrint = CONF.getProperty("gateway.isPrint",   "2");
        String gwUserId  = CONF.getProperty("gateway.userId",    "");
        String gwToken   = CONF.getProperty("gateway.token",     "");
        String gwStation = CONF.getProperty("gateway.stationId", "");
        int threadCount  = Integer.parseInt(CONF.getProperty("compensation.threadCount", "11"));

        GwtjReportService service = new GwtjReportService();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount    = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (SyncTask task : tasks) {
            if (task.getHospitalFid() == null || task.getMonth() == null) {
                log.warn("[补偿-dataPrepare] 跳过：hospitalFid 或 month 为空 id={}", task.getId());
                failCount.incrementAndGet();
                continue;
            }
            executor.submit(() -> {
                String result = service.reprocessByDataPrepare(
                        task, gwFid, gwIsPrint, gwUserId, gwToken, gwStation);
                if (result.startsWith("success")) {
                    successCount.incrementAndGet();
                } else {
                    failCount.incrementAndGet();
                }
            });
        }
        executor.shutdown();
        try {
            boolean finished = executor.awaitTermination(4, TimeUnit.HOURS);
            if (!finished) log.warn("[补偿-dataPrepare] 部分任务超时未完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[补偿-dataPrepare] 等待线程池结束时被中断", e);
        }
        log.info("[补偿-dataPrepare] 全部执行完毕: 总计={}, 成功={}, 失败={}",
                tasks.size(), successCount.get(), failCount.get());
    }

    // ─────────────────────────────────────────────────────────────────────
    // 私有查询方法
    // ─────────────────────────────────────────────────────────────────────

    /** 查询 status=SUCCESS 且 total_count=0 的任务 */
    private List<SyncTask> selectSuccessWithZeroTotal() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            return session.getMapper(SyncTaskMapper.class).selectSuccessWithZeroTotal();
        }
    }

    /** 查询所有 total_count=0 的任务（不限状态） */
    private List<SyncTask> selectAllWithZeroTotal() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            return session.getMapper(SyncTaskMapper.class).selectAllWithZeroTotal();
        }
    }
}
