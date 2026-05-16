package com.helianhealth.family.he.admin.service.syncreport;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.helianhealth.family.he.admin.model.report.param.CustomerAndManualReportParam;
import com.helianhealth.family.he.admin.model.report.param.CustomerArchiveCreateParam;
import com.helianhealth.family.he.admin.model.report.param.ManualFillReportParam;
import com.helianhealth.family.he.admin.model.report.param.ManualResolveReportDto;
import com.helianhealth.family.he.admin.model.report.param.ResolveReportItemParam;
import com.helianhealth.family.he.admin.model.report.param.ResolveReportNodeParam;
import com.helianhealth.family.he.admin.model.wgtj.*;
import com.helianhealth.family.he.admin.db.MyBatisUtil;
import com.helianhealth.family.he.admin.db.entity.SyncFailure;
import com.helianhealth.family.he.admin.db.entity.SyncTask;
import com.helianhealth.family.he.admin.db.mapper.SyncFailureMapper;
import com.helianhealth.family.he.admin.db.mapper.SyncTaskMapper;

import com.helianhealth.family.he.admin.util.CodeFunctionMapping;
import com.helianhealth.family.he.admin.util.HttpsClientUtils;
import com.helianhealth.family.he.admin.util.TokenManager;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.ibatis.session.SqlSession;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class GwtjReportService {
    /**
     * 1.1 获取访问令牌
     * 2.1 查询医院机构信息   只查询一次就行,绿园区医院只与3家基层机构有关
     * 2.3 按月份准备体检人员数据
     * 2.4 查询任务详情
     * 2.10 文件下载
     */
    private static final String SERVER_URL = "https://gwjkfw.jljcws.com:8443/hcclyq";
    private static final String HOSPITAL_QUERY_URL = SERVER_URL + "/api/v1/manbing/yiyuan/query";
    private static final String DATA_PREPARE_MONTH_URL = SERVER_URL + "/api/v1/manbing/dataPrepare/byMonth";
    private static final String DATA_PREPARE_DATE_URL = SERVER_URL + "/api/v1/manbing/dataPrepare/byDate";
    private static final String TASK_QUERY_URL = SERVER_URL + "/api/v1/manbing/task/query";
    private static final String DOWNLOAD_URL = SERVER_URL + "/api/v1/manbing/file/download";
    private static final Gson GSON = new Gson();
    /** 线程安全的日期格式化器 */
    private static final ThreadLocal<SimpleDateFormat> SDF =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    /** 从 db.properties 加载的运行配置 */
    private static final Properties CONF = loadConf();

    private static Properties loadConf() {
        Properties p = new Properties();
        try (InputStream in = GwtjReportService.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) p.load(in);
        } catch (IOException e) {
            // 配置加载失败，后续使用默认值
        }
        return p;
    }


    public String syncReport(String hospitalFid, String hospitalName, String month, String fid, String isPrint,
                                     String userId, String token, String stationId) {
        // 1. 检查是否已成功同步：幂等跳过 + 失败可重跑
        SyncTask task = ensureTaskRecord(hospitalFid, hospitalName, month);
        if (task == null) {
            // 已 SUCCESS，跳过
            return "skip";
        }

        int size = 0;
        int successCount = 0;
        int failCount = 0;
        try {
            List<CustomerAndManualReportParam> result = syncHospitalReport(month, hospitalFid, fid, hospitalName, task);
            // 任务超时已在 syncHospitalReport 内写表标记 TIMEOUT，这里直接返回，避免被覆盖为 SUCCESS
            if (result == null) {
                return "timeout";
            }
            size = result.size();
            // 调用检后方法处理
            for (CustomerAndManualReportParam param : result) {
                param.getArchiveCreateParam().setLastReportStation(hospitalName);
                if ("1".equals(isPrint)) {
                    String prettyJson = JSON.toJSONString(param, com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat);
                    log.info("调用建档接口前：{}", prettyJson);
                    successCount++;
                } else {
                    try {
                        sendToGateway(param, userId, token, stationId);
                        successCount++;
                    } catch (Exception e) {
                        failCount++;
                        log.error("推送失败, name={}", param.getArchiveCreateParam().getName(), e);
                        recordFailure(task.getId(), hospitalFid, month, param, "PUSH_GATEWAY", e.getMessage());
                    }
                }
            }
            // 任务整体成功收尾（即使部分明细失败也算 SUCCESS，失败明细可单独重试）
            updateTaskResult(task.getId(), "SUCCESS", null, size, successCount, failCount);
        } catch (Exception e) {
            log.error("syncReport error", e);
            updateTaskResult(task.getId(), "FAILED", e.getMessage(), size, successCount, failCount);
        }
        return "success:" + successCount + "/" + size;
    }

    /**
     * 补偿入口：利用数据库中已存储的 taskId 和 zipPwd，
     * 直接下载 → 解析 → 推送，跳过 dataPrepare/taskQuery 步骤。
     * 适用于 status=SUCCESS 但 total_count=0 的任务补偿。
     *
     * @param task      已存储 taskId/zipPwd 的任务记录
     * @param fid       网关机构 fid
     * @param isPrint   "1"=仅打印不推送，其他=真实推送
     * @param userId    网关 userId
     * @param token     网关 token
     * @param stationId 网关 stationId
     */
    public String reprocessByStoredTask(SyncTask task, String fid, String isPrint,
                                        String userId, String token, String stationId) {
        String taskId = task.getTaskId();
        String zipPwd = task.getZipPwd();
        if (taskId == null || zipPwd == null) {
            log.error("[补偿] task.taskId 或 task.zipPwd 为空，无法补偿 id={}", task.getId());
            return "error:no_taskId_or_zipPwd";
        }
        int size = 0, successCount = 0, failCount = 0;
        try {
            log.info("[补偿] 开始下载文件 taskId={}, hospital={}, month={}",
                    taskId, task.getHospitalName(), task.getMonth());
            List<GwtjRecord> records = downloadAndHandleFile(taskId, zipPwd, task);
            List<CustomerAndManualReportParam> params =
                    buildReportParam(records, fid, task.getHospitalName());
            size = params.size();
            log.info("[补偿] 解析完成，共 {} 条记录，开始推送", size);
            for (CustomerAndManualReportParam param : params) {
                param.getArchiveCreateParam().setLastReportStation(task.getHospitalName());
                if ("1".equals(isPrint)) {
                    String prettyJson = JSON.toJSONString(param,
                            com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat);
                    log.info("[补偿仅打印] 建档参数: {}", prettyJson);
                    successCount++;
                } else {
                    try {
                        sendToGateway(param, userId, token, stationId);
                        successCount++;
                        log.info("[补偿] 推送成功 name={}", param.getArchiveCreateParam().getName());
                    } catch (Exception e) {
                        failCount++;
                        log.error("[补偿] 推送失败 name={}", param.getArchiveCreateParam().getName(), e);
                        recordFailure(task.getId(), task.getHospitalFid(), task.getMonth(),
                                param, "PUSH_GATEWAY", e.getMessage());
                    }
                }
            }
            updateTaskResult(task.getId(), "SUCCESS", null, size, successCount, failCount);
            log.info("[补偿] 完成 hospital={}, month={}, 总计={}, 成功={}, 失败={}",
                    task.getHospitalName(), task.getMonth(), size, successCount, failCount);
        } catch (Exception e) {
            log.error("[补偿] 重处理失败 hospital={}, month={}", task.getHospitalName(), task.getMonth(), e);
            updateTaskResult(task.getId(), "FAILED", e.getMessage(), size, successCount, failCount);
            return "error:" + e.getMessage();
        }
        return "success:" + successCount + "/" + size;
    }

    /**
     * 补偿入口（重新走 dataPrepare 全流程）：
     * 重新调用 dataPrepareMonth 提交任务 → 轮询达到 SUCCESS → 下载解压 → 解析 → 推送。
     * 适用于 total_count=0 且不依赖已存 taskId/zipPwd 的情况（比如旧数据不常存或已失效）。
     *
     * @param task      已存 hospital_fid/hospital_name/month 的任务记录
     * @param fid       网关机构 fid
     * @param isPrint   "1"=仅打印不推送，其他=真实推送
     * @param userId    网关 userId
     * @param token     网关 token
     * @param stationId 网关 stationId
     */
    public String reprocessByDataPrepare(SyncTask task, String fid, String isPrint,
                                         String userId, String token, String stationId) {
        String hospitalFid  = task.getHospitalFid();
        String hospitalName = task.getHospitalName();
        String month        = task.getMonth();
        int size = 0, successCount = 0, failCount = 0;
        try {
            log.info("[补偿-dataPrepare] 开始重跑 hospital={}, month={}", hospitalName, month);
            List<CustomerAndManualReportParam> result =
                    syncHospitalReport(month, hospitalFid, fid, hospitalName, task);
            if (result == null) {
                // syncHospitalReport 内部已将该任务标记为 TIMEOUT
                log.warn("[补偿-dataPrepare] 超时 hospital={}, month={}", hospitalName, month);
                return "timeout";
            }
            size = result.size();
            log.info("[补偿-dataPrepare] 解析完成，共 {} 条记录，开始推送", size);
            for (CustomerAndManualReportParam param : result) {
                param.getArchiveCreateParam().setLastReportStation(hospitalName);
                if ("1".equals(isPrint)) {
                    String prettyJson = JSON.toJSONString(param,
                            com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat);
                    log.info("[补偿仅打印] 建档参数: {}", prettyJson);
                    successCount++;
                } else {
                    try {
                        sendToGateway(param, userId, token, stationId);
                        successCount++;
                        log.info("[补偿-dataPrepare] 推送成功 name={}", param.getArchiveCreateParam().getName());
                    } catch (Exception e) {
                        failCount++;
                        log.error("[补偿-dataPrepare] 推送失败 name={}", param.getArchiveCreateParam().getName(), e);
                        recordFailure(task.getId(), hospitalFid, month, param, "PUSH_GATEWAY", e.getMessage());
                    }
                }
            }
            updateTaskResult(task.getId(), "SUCCESS", null, size, successCount, failCount);
            log.info("[补偿-dataPrepare] 完成 hospital={}, month={}, 总计={}, 成功={}, 失败={}",
                    hospitalName, month, size, successCount, failCount);
        } catch (Exception e) {
            log.error("[补偿-dataPrepare] 重处理失败 hospital={}, month={}", hospitalName, month, e);
            updateTaskResult(task.getId(), "FAILED", e.getMessage(), size, successCount, failCount);
            return "error:" + e.getMessage();
        }
        return "success:" + successCount + "/" + size;
    }

    /**
     * 确保任务记录存在并标记为 RUNNING；若已 SUCCESS 返回 null（调用方应跳过）。
     */
    private SyncTask ensureTaskRecord(String hospitalFid, String hospitalName, String month) {
        try (SqlSession session = MyBatisUtil.openSession()) {
            SyncTaskMapper mapper = session.getMapper(SyncTaskMapper.class);
            SyncTask task = mapper.selectByHospitalAndMonth(hospitalFid, month);
            if (task != null && "SUCCESS".equals(task.getStatus())) {
                log.info("跳过已成功的任务 hospital={}, month={}", hospitalName, month);
                return null;
            }
            if (task == null) {
                task = new SyncTask();
                task.setHospitalFid(hospitalFid);
                task.setHospitalName(hospitalName);
                task.setMonth(month);
                task.setStatus("RUNNING");
                task.setTotalCount(0);
                task.setSuccessCount(0);
                task.setFailCount(0);
                task.setRetryTimes(0);
                mapper.insert(task);
            } else {
                task.setStatus("RUNNING");
                task.setRetryTimes((task.getRetryTimes() == null ? 0 : task.getRetryTimes()) + 1);
                mapper.update(task);
            }
            return task;
        } catch (Exception ex) {
            log.error("任务记录写入异常 hospital={}, month={}", hospitalFid, month, ex);
            // 持久化异常不阻塞业务，返回一个临时对象继续执行
            SyncTask t = new SyncTask();
            t.setId(-1L);
            t.setHospitalFid(hospitalFid);
            t.setHospitalName(hospitalName);
            t.setMonth(month);
            return t;
        }
    }

    /** 记录单条推送失败明细 */
    private void recordFailure(Long taskId, String hospitalFid, String month,
                               CustomerAndManualReportParam param, String stage, String errMsg) {
        if (taskId == null || taskId < 0) return;
        try (SqlSession session = MyBatisUtil.openSession()) {
            SyncFailure f = new SyncFailure();
            f.setTaskIdRef(taskId);
            f.setHospitalFid(hospitalFid);
            f.setMonth(month);
            CustomerArchiveCreateParam archive = param.getArchiveCreateParam();
            if (archive != null) {
                f.setCustomerName(archive.getName());
                try {
                    // 不同版本字段不一致，反射兜底，避免编译期耦合
                    java.lang.reflect.Method m = archive.getClass().getMethod("getIdNo");
                    Object idno = m.invoke(archive);
                    if (idno != null) f.setCustomerIdno(String.valueOf(idno));
                } catch (Exception ignore) { }
            }
            f.setStage(stage);
            f.setPayload(GSON.toJson(param));
            f.setErrorMsg(truncate(errMsg, 2000));
            f.setRetryTimes(0);
            f.setResolved(0);
            session.getMapper(SyncFailureMapper.class).insert(f);
        } catch (Exception ex) {
            log.error("记录失败明细异常", ex);
        }
    }

    /**
     * 标记任务为 TIMEOUT，并记录远端 jobid，便于后续人工/重跑追溯。
     * 不抛异常：持久化失败仅落日志，不影响主流程跳过当前 month。
     */
    private void markTaskTimeout(SyncTask task, String remoteTaskId) {
        if (task == null || task.getId() == null || task.getId() < 0) return;
        try (SqlSession session = MyBatisUtil.openSession()) {
            SyncTaskMapper mapper = session.getMapper(SyncTaskMapper.class);
            SyncTask cur = new SyncTask();
            cur.setId(task.getId());
            cur.setStatus("TIMEOUT");
            cur.setTaskId(remoteTaskId);
            cur.setErrorMsg(truncate("任务超时未完成 taskId=" + remoteTaskId, 2000));
            cur.setTotalCount(0);
            cur.setSuccessCount(0);
            cur.setFailCount(0);
            cur.setRetryTimes(0);
            mapper.update(cur);
            log.warn("任务超时已记录 sync_task.id={}, remoteTaskId={}", task.getId(), remoteTaskId);
        } catch (Exception ex) {
            log.error("标记任务超时异常 task={}, remoteTaskId={}", task.getId(), remoteTaskId, ex);
        }
    }

    /** 更新任务整体状态 */
    private void updateTaskResult(Long id, String status, String errMsg,
                                  int total, int success, int fail) {
        if (id == null || id < 0) return;
        try (SqlSession session = MyBatisUtil.openSession()) {
            SyncTaskMapper mapper = session.getMapper(SyncTaskMapper.class);
            SyncTask cur = new SyncTask();
            cur.setId(id);
            cur.setStatus(status);
            cur.setErrorMsg(truncate(errMsg, 2000));
            cur.setTotalCount(total);
            cur.setSuccessCount(success);
            cur.setFailCount(fail);
            cur.setRetryTimes(0);
            mapper.update(cur);
        } catch (Exception ex) {
            log.error("更新任务状态异常 id={}", id, ex);
        }
    }

    /**
     * 指定单个 JSON 文件路径，手动解析并推送到网关。
     * 适用于 JSON_PARSE 类型失败后，人工修复文件内容再重新处理的场景。
     *
     * @param jsonFilePath JSON 文件绝对路径
     * @param fid          网关机构 fid
     * @param hospitalName 医院名称（用于归档站点）
     * @param userId       网关 userId
     * @param token        网关 token
     * @param stationId    网关 stationId
     * @param isPrint      "1"=仅打印不推送，其他=真实推送
     */
    public void processJsonFile(String jsonFilePath, String fid, String hospitalName,
                                String userId, String token, String stationId, String isPrint) {
        Path jsonFile = Paths.get(jsonFilePath);
        if (!Files.exists(jsonFile)) {
            log.error("指定的 JSON 文件不存在: {}", jsonFilePath);
            return;
        }
        List<GwtjRecord> records;
        try {
            String content = new String(Files.readAllBytes(jsonFile), StandardCharsets.UTF_8);
            records = GSON.fromJson(content, new TypeToken<List<GwtjRecord>>() {}.getType());
            log.info("JSON文件解析完成: {}, 记录数: {}", jsonFile.getFileName(), records.size());
        } catch (Exception e) {
            log.error("JSON文件解析失败: {}", jsonFilePath, e);
            return;
        }
        List<CustomerAndManualReportParam> params;
        try {
            params = buildReportParam(records, fid, hospitalName);
        } catch (Exception e) {
            log.error("构建报告参数失败: {}", jsonFilePath, e);
            return;
        }
        int successCount = 0, failCount = 0;
        for (CustomerAndManualReportParam param : params) {
            param.getArchiveCreateParam().setLastReportStation(hospitalName);
            if ("1".equals(isPrint)) {
                String prettyJson = JSON.toJSONString(param, com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat);
                log.info("[仅打印] 建档参数: {}", prettyJson);
                successCount++;
            } else {
                try {
                    sendToGateway(param, userId, token, stationId);
                    successCount++;
                    log.info("推送成功 name={}", param.getArchiveCreateParam().getName());
                } catch (Exception e) {
                    failCount++;
                    log.error("推送失败 name={}", param.getArchiveCreateParam().getName(), e);
                }
            }
        }
        log.info("processJsonFile 完成: 文件={}, 总计={}, 成功={}, 失败={}",
                jsonFile.getFileName(), params.size(), successCount, failCount);
    }

    /**
     * 重跑所有失败的明细（payload 直接反序列化后调用网关）
     */
    public void retryFailures(String userId, String token, String stationId) {
        try (SqlSession session = MyBatisUtil.openSession()) {
            SyncFailureMapper mapper = session.getMapper(SyncFailureMapper.class);
            List<SyncFailure> list = mapper.selectUnresolved();
            log.info("待重试失败记录: {} 条", list.size());
            for (SyncFailure f : list) {
                if ("JSON_PARSE".equals(f.getStage())) {
                    log.warn("JSON_PARSE 类型失败需人工处理，跳过重试 id={}, payload={}", f.getId(), f.getPayload());
                    continue;
                }
                try {
                    CustomerAndManualReportParam p = GSON.fromJson(f.getPayload(), CustomerAndManualReportParam.class);
                    sendToGateway(p, userId, token, stationId);
                    mapper.markResolved(f.getId());
                    log.info("重试成功 id={}, name={}", f.getId(), f.getCustomerName());
                } catch (Exception e) {
                    mapper.incrementRetry(f.getId());
                    log.error("重试失败 id={}, name={}", f.getId(), f.getCustomerName(), e);
                }
            }
        }
    }

    /** 将远端 taskId 和 ZIP 密码就地持久化，失败不阻断主流程 */
    private void saveTaskMeta(SyncTask task) {
        if (task == null || task.getId() == null || task.getId() < 0) return;
        try (SqlSession session = MyBatisUtil.openSession()) {
            session.getMapper(SyncTaskMapper.class).updateMeta(task.getId(), task.getTaskId(), task.getZipPwd());
            log.info("任务元数据已保存 id={}, taskId={}", task.getId(), task.getTaskId());
        } catch (Exception ex) {
            log.warn("保存任务元数据失败 id={}", task.getId(), ex);
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }

    private List<CustomerAndManualReportParam> syncHospitalReport(String month, String hospitalFid, String fid, String hospitalName, SyncTask task) throws Exception {
        String taskId = this.dataPrepareMonth(month, hospitalFid);
        int maxRetry = 20;
        TaskQueryResponse.TaskData taskData = null;
        for (int i = 0; i < maxRetry; i++) {
            TaskQueryResponse taskQueryResponse = this.taskQuery(taskId);
            taskData = taskQueryResponse.getData();
            String jobState = taskData.getJobState();
            log.info("任务状态查询 taskId={}, jobState={}, 第{}次", taskId, jobState, i + 1);
            if ("SUCCESS".equals(jobState)) {
                break;
            }
            if (i == maxRetry - 1) {
                // 超时不抛异常：写入 sync_task 表，状态置为 TIMEOUT，主流程返回 null 让上层跳过本月
                markTaskTimeout(task, taskId);
                return null;
            }
            Thread.sleep(1000 * 5);
        }
        String pwd = taskData.getZipPassword();
        // 获取到 taskId 和密码后立即写入数据库，便于后续异常时可查到对应文件
        task.setTaskId(taskId);
        task.setZipPwd(pwd);
        saveTaskMeta(task);
        List<GwtjRecord> gwtjRecords = this.downloadAndHandleFile(taskId, pwd, task);
        return this.buildReportParam(gwtjRecords, fid, hospitalName);
    }

    /**
     * 查询医院机构列表
     * 自动通过TokenManager获取有效token，调用方无需关心token刷新
     */
    public HospitalQueryResponse queryHospitals() throws Exception {
        String accessToken = TokenManager.getInstance().getValidToken();
        try (CloseableHttpClient httpClient = HttpsClientUtils.createHttpsClient()) {
            HttpGet httpGet = new HttpGet(HOSPITAL_QUERY_URL);

            // 设置请求头
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("apitoken", accessToken);

            log.info("发起医院查询请求");

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getCode();

                log.info("响应状态码: {}", statusCode);
                log.info("响应内容: {}", responseBody);

                if (statusCode == 200) {
                    return GSON.fromJson(responseBody, HospitalQueryResponse.class);
                } else {
                    throw new RuntimeException("查询医院列表失败，状态码: " + statusCode + ", 响应: " + responseBody);
                }
            }
        }
    }


    private String dataPrepareMonth(String month, String hospitalFid) throws Exception {
        return dataPrepare(month, hospitalFid, DATA_PREPARE_MONTH_URL);
    }

    private String dataPrepareDate(String date, String hospitalFid) throws Exception {
        return dataPrepare(date, hospitalFid, DATA_PREPARE_DATE_URL);
    }

    private String dataPrepare(String date, String hospitalFid, String url) throws Exception {
        log.info("获取数据准备任务 url={}, date={}, hospitalFid={}", url, date, hospitalFid);
        String accessToken = TokenManager.getInstance().getValidToken();

        try (CloseableHttpClient httpClient = HttpsClientUtils.createHttpsClient()) {
            HttpPost httpPost = new HttpPost(new URIBuilder(url)
                    .addParameter("tijianRq", date)
                    .addParameter("yiyuanFid", hospitalFid)
                    .build());
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("apitoken", accessToken);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getCode();
                log.info("响应状态码: {}, 响应内容: {}", statusCode, responseBody);
                if (statusCode != 200) {
                    throw new RuntimeException("HTTP请求失败，状态码: " + statusCode + ", 响应: " + responseBody);
                }
                JsonObject result = GSON.fromJson(responseBody, JsonObject.class);
                String code = result.get("code").getAsString();
                String message = result.get("message").getAsString();
                if (!"200".equals(code)) {
                    throw new RuntimeException("任务提交失败: " + message);
                }
                return result.getAsJsonObject("data").get("jobid").getAsString();
            }
        }
    }

    private TaskQueryResponse taskQuery(String taskId) throws Exception {
        log.info("查询任务详情 taskId={}", taskId);
        String accessToken = TokenManager.getInstance().getValidToken();

        try (CloseableHttpClient httpClient = HttpsClientUtils.createHttpsClient()) {
            HttpGet httpGet = new HttpGet(new URIBuilder(TASK_QUERY_URL).addParameter("jobid", taskId).build());
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("apitoken", accessToken);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getCode();
                log.info("响应状态码: {}, 响应内容: {}", statusCode, responseBody);
                if (statusCode == 200) {
                    return GSON.fromJson(responseBody, TaskQueryResponse.class);
                } else {
                    throw new RuntimeException("查询任务详情失败，状态码: " + statusCode + ", 响应: " + responseBody);
                }
            }
        }
    }

    private List<GwtjRecord> downloadAndHandleFile(String taskId, String pwd, SyncTask task) throws Exception {
        String accessToken = TokenManager.getInstance().getValidToken();

        // 使用项目目录下的 temp 文件夹存放 ZIP 及解压目录
        Path tempRoot = Paths.get(System.getProperty("user.dir"), "temp");
        Files.createDirectories(tempRoot);
        // 用 hospitalFid + month 命名，方便后续按医院/月份定位文件
        String hospitalFid = task != null && task.getHospitalFid() != null ? task.getHospitalFid() : "unknown";
        String month       = task != null && task.getMonth()       != null ? task.getMonth()       : "unknown";
        String baseName    = hospitalFid + "_" + month;
        Path tempZip = tempRoot.resolve(baseName + ".zip");
        // 解压目录
        Path tempDir = tempRoot.resolve(baseName);
        // 解压前清空目录，避免残留旧文件被混入解析
        if (Files.exists(tempDir)) {
            try (java.util.stream.Stream<Path> walk = Files.walk(tempDir)) {
                walk.sorted(java.util.Comparator.reverseOrder())
                    .filter(p -> !p.equals(tempDir))
                    .forEach(p -> { try { Files.delete(p); } catch (Exception ignore) {} });
            }
        }
        Files.createDirectories(tempDir);
        List<GwtjRecord> result = new java.util.ArrayList<>();
        try {
            try (CloseableHttpClient httpClient = HttpsClientUtils.createHttpsClient()) {
                HttpGet httpGet = new HttpGet(new URIBuilder(DOWNLOAD_URL).addParameter("jobid", taskId).build());
                httpGet.setHeader("Content-Type", "application/json");
                httpGet.setHeader("apitoken", accessToken);

                try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                    int statusCode = response.getCode();
                    log.info("响应状态码: {}", statusCode);
                    // 打印关键响应头，判断是否返回了正确的文件流

                    if (statusCode != 200) {
                        String errBody = EntityUtils.toString(response.getEntity());
                        log.error("文件下载失败，状态码: {}, 响应体: {}", statusCode, errBody);
                        throw new RuntimeException("文件下载失败，状态码: " + statusCode);
                    }
                    // 响应流写入临时 ZIP
                    try (InputStream in = response.getEntity().getContent();
                         OutputStream out = Files.newOutputStream(tempZip)) {
                        byte[] buf = new byte[8192];
                        int len;
                        while ((len = in.read(buf)) != -1) {
                            out.write(buf, 0, len);
                        }
                    }
                }
            }

            // 解压到临时目录，逐个读取 JSON 文件，避免内存压力
            log.info("开始解压文件: {}, 密码: {}", tempZip.getFileName(), pwd);
            try (ZipFile zipFile = new ZipFile(tempZip.toFile(), pwd.toCharArray())) {
                zipFile.extractAll(tempDir.toString());
            }
            try (java.util.stream.Stream<Path> stream = Files.walk(tempDir)) {
                stream.filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".json"))
                        .forEach(jsonFile -> {
                            try {
                                String content = new String(Files.readAllBytes(jsonFile), StandardCharsets.UTF_8);
                                List<GwtjRecord> records = GSON.fromJson(content, new TypeToken<List<GwtjRecord>>() {
                                }.getType());
                                log.info("JSON文件: {}, 解析记录数: {}", jsonFile.getFileName(), records.size());
                                result.addAll(records);
                            } catch (Exception e) {
                                log.error("读取JSON文件失败: {}", jsonFile.getFileName(), e);
                                // 解析失败：写入 sync_failure 表，payload 记录文件绝对路径，便于人工排查
                                try (SqlSession session = MyBatisUtil.openSession()) {
                                    SyncFailure f = new SyncFailure();
                                    f.setTaskIdRef(task != null && task.getId() != null && task.getId() >= 0 ? task.getId() : null);
                                    f.setHospitalFid(task != null ? task.getHospitalFid() : null);
                                    f.setMonth(task != null ? task.getMonth() : null);
                                    f.setStage("JSON_PARSE");
                                    f.setPayload(jsonFile.toAbsolutePath().toString());
                                    f.setErrorMsg(truncate(e.getMessage(), 2000));
                                    f.setRetryTimes(0);
                                    f.setResolved(0);
                                    session.getMapper(SyncFailureMapper.class).insert(f);
                                } catch (Exception dbEx) {
                                    log.error("记录JSON解析失败明细异常", dbEx);
                                }
                            }
                        });
            }
        } finally {
            // 解压目录及 JSON 文件均保留，供人工追溯
            log.info("解压目录已保留: {}, ZIP: {}", tempDir.toAbsolutePath(), tempZip.getFileName());
        }
        return result;
    }

    private List<CustomerAndManualReportParam> buildReportParam(List<GwtjRecord> records, String fid, String hospitalName) throws ParseException {
        if (CollectionUtil.isEmpty(records)) {
            return CollectionUtil.empty(List.class);
        }
        List<ResolveReportItemParam> templateClass = getTemplateClass();
        List<CustomerAndManualReportParam> result = new ArrayList<>();
        for (GwtjRecord record : records) {
            List<ResolveReportItemParam> templateClassCopy = templateClass.stream().map(item -> {
                        ResolveReportItemParam itemCopy = BeanUtil.copyProperties(item, ResolveReportItemParam.class);
                        if (CollectionUtil.isNotEmpty(item.getReportNodeList())) {
                            itemCopy.setReportNodeList(item.getReportNodeList().stream()
                                    .map(node -> BeanUtil.copyProperties(node, ResolveReportNodeParam.class))
                                    .collect(Collectors.toList()));
                        }
                        return itemCopy;
                    })
                    .collect(Collectors.toList());

            CustomerAndManualReportParam param = new CustomerAndManualReportParam();
            param.setStationId(fid);

            DanganInfo danganInfo = record.getDanganInfo();
            TijianInfo tijianInfo = record.getTijianInfo();

            // 组装档案参数
            CustomerArchiveCreateParam archiveCreateParam = new CustomerArchiveCreateParam();
            archiveCreateParam.setHeight(StrUtil.isNotEmpty(tijianInfo.getShengao()) ? Double.valueOf(tijianInfo.getShengao()) : null);
            archiveCreateParam.setWeight(StrUtil.isNotEmpty(tijianInfo.getTizhong()) ? Double.valueOf(tijianInfo.getTizhong()) : null);
            archiveCreateParam.setMarried(convertMarried(danganInfo.getHunyinZk()));
            archiveCreateParam.setMobile(danganInfo.getBenrenLxdh());
            archiveCreateParam.setName(danganInfo.getXingming());
            archiveCreateParam.setIdCardNo(danganInfo.getShenfenZh());
            archiveCreateParam.setIdCardType(1);
            archiveCreateParam.setIcdAuto(false);
            archiveCreateParam.setAge(Integer.valueOf(danganInfo.getNianlingYear()));
            archiveCreateParam.setCompany(danganInfo.getJiandangDw());
            param.setArchiveCreateParam(archiveCreateParam);

            // 组装报告参数
            ManualFillReportParam reportParam = new ManualFillReportParam();
            if (StrUtil.isNotEmpty(tijianInfo.getCreateTime())) {
                reportParam.setCheckTime(ensureDateTimeFormat(tijianInfo.getCreateTime()));
            } else {
                reportParam.setCheckTime(StrUtil.isBlank(danganInfo.getJiandangRq()) ? SDF.get().format(new Date()) : ensureDateTimeFormat(danganInfo.getJiandangRq()));
            }
            // 生成具体的报告参数
            for (ResolveReportItemParam item : templateClassCopy) {
                String itemId = item.getItemId();
                List<ResolveReportNodeParam> reportNodeList = item.getReportNodeList();
                if (CollectionUtil.isEmpty(reportNodeList)) {
                    continue;
                }
                reportNodeList.forEach(node -> {
                    String nodeId = node.getNodeId();
                    node.setNodeValue(CodeFunctionMapping.getFunction(itemId, nodeId, tijianInfo));
                });
                // 移除 nodeValue 为 null 的节点
                // reportNodeList.removeIf(node -> StrUtil.isEmpty(node.getNodeValue()));
                item.setReportNodeList(reportNodeList);
            }
            // 移除所有节点都为空的 item
            templateClassCopy.removeIf(item -> CollectionUtil.isEmpty(item.getReportNodeList()));
            reportParam.setReportItemList(templateClassCopy);
            ManualResolveReportDto.Conclusion conclusion = new ManualResolveReportDto.Conclusion();
            conclusion.setLevel(1);

            // 设置具体的建议和结论
            conclusion.setSummary(buildText(
                    "健康评价:", tijianInfo.getJiankangPl(),
                    "健康评价有异常1:", tijianInfo.getJiankangPlyyc(),
                    "健康评价有异常2:", tijianInfo.getJiankangPlyyc2(),
                    "健康评价有异常3:", tijianInfo.getJiankangPlyyc3(),
                    "健康评价有异常4:", tijianInfo.getJiankangPlyyc4()
            ));
            conclusion.setSuggest(buildText(
                    "健康指导:", tijianInfo.getJiankangZd(),
                    "危险因素控制:", tijianInfo.getWeixianYskz(),
                    "减体重目标:", tijianInfo.getJiantiZmb(),
                    "建议接种疫苗名称:", tijianInfo.getJianyiJzym(),
                    "危险因素控制其他:", tijianInfo.getWeixianYskzqt()
            ));
            reportParam.setConclusions(CollectionUtil.newArrayList(conclusion));
            reportParam.setStationName(danganInfo.getJiandangDw());
            param.setManualFillReportParam(reportParam);
            result.add(param);
        }
        return result;
    }


    private List<ResolveReportItemParam> getTemplateClass() {
        try (InputStream in = GwtjReportService.class.getClassLoader().getResourceAsStream("template_report.json")) {
            if (in == null) {
                throw new RuntimeException("template_report.json not found in resources");
            }
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            List<ResolveReportItemParam> template = GSON.fromJson(content, new TypeToken<List<ResolveReportItemParam>>() {
            }.getType());

            return template;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildText(String... labelAndValues) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < labelAndValues.length; i += 2) {
            if (StrUtil.isNotBlank(labelAndValues[i + 1])) {
                sb.append(labelAndValues[i]).append(labelAndValues[i + 1]).append('\n');
            }
        }
        return sb.toString();
    }

    /** hunyinZk: 2-已婚，3-丧偶，4-离婚 */
    private Integer convertMarried(String hunyinZk) {
        switch (hunyinZk) {
            case "2":
                return 1;
            case "4":
                return 2;
            case "3":
                return 3;
            default:
                return 0;
        }
    }

    private String ensureDateTimeFormat(String dateStr) {
        if (StrUtil.isBlank(dateStr)) {
            return SDF.get().format(new Date());
        }
        dateStr = dateStr.trim();
        if (dateStr.length() == 10) {
            return dateStr + " 00:00:00";
        }
        if (dateStr.length() == 19) {
            return dateStr;
        }
        try {
            Date parsed = SDF.get().parse(dateStr);
            return SDF.get().format(parsed);
        } catch (ParseException e) {
            return SDF.get().format(new Date());
        }
    }

    private static final String ADD_CUSTOMER_URL = "https://ark-gw.helianhealth.com/admin/customerManage/addCustomerAndManualReport";

    private void sendToGateway(CustomerAndManualReportParam param, String userId, String token, String stationId) throws Exception {
        String body = GSON.toJson(param);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(ADD_CUSTOMER_URL);
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.setHeader("userid", userId);
            httpPost.setHeader("token", token);
            httpPost.setHeader("stationId", stationId);
            httpPost.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(body, org.apache.hc.core5.http.ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                int statusCode = response.getCode();
                log.info("建档接口响应 statusCode={}, name={}", statusCode, param.getArchiveCreateParam().getName());
                if (statusCode != 200) {
                    throw new RuntimeException("建档接口请求失败，状态码: " + statusCode + ", 响应: " + responseBody);
                }
            }
        }
    }

    public void syncReport() {
        try {
            HospitalQueryResponse hospitalResponse = this.queryHospitals();
            List<HospitalData> hospitals = hospitalResponse.getData();
            if (CollectionUtil.isEmpty(hospitals)) {
                log.info("没有可同步的医院");
                return;
            }

            ExecutorService executor = Executors.newFixedThreadPool(hospitals.size());
            // 按配置从 sync.startMonth 倒序遍历到 sync.endMonth
            String startMonthStr = CONF.getProperty("sync.startMonth", "2019-10");
            String endMonthStr   = CONF.getProperty("sync.endMonth",   "2015-01");
            final String gwFid     = CONF.getProperty("gateway.fid",      "HL07731");
            final String gwIsPrint = CONF.getProperty("gateway.isPrint",  "2");
            final String gwUserId  = CONF.getProperty("gateway.userId",   "");
            final String gwToken   = CONF.getProperty("gateway.token",    "");
            final String gwStation = CONF.getProperty("gateway.stationId","");
            List<String> months = new ArrayList<>();
            LocalDate cursor = LocalDate.parse(startMonthStr + "-01");
            LocalDate end = LocalDate.parse(endMonthStr + "-01");
            DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("yyyy-MM");
            while (!cursor.isBefore(end)) {
                months.add(cursor.format(monthFmt));
                cursor = cursor.minusMonths(1);
            }
            for (HospitalData hospital : hospitals) {
                final String hospitalFid = hospital.getYiyuanFid();
                final String hospitalName = hospital.getYiyuanMc();
                executor.submit(() -> {
                    for (String month : months) {
                        log.info("开始同步 hospital={}, month={}", hospitalFid, month);
                        this.syncReport(
                                hospitalFid, hospitalName,
                                month, gwFid, gwIsPrint,
                                gwUserId, gwToken, gwStation
                        );
                    }
                });
            }
            executor.shutdown();
            boolean finished = executor.awaitTermination(2, TimeUnit.HOURS);
            if (!finished) {
                log.warn("部分医院同步任务超时未完成");
            }
        } catch (Exception e) {
            log.error("syncReport 查询医院列表失败", e);
        }
    }
}