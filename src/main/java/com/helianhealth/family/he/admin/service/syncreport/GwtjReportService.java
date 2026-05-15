package com.helianhealth.family.he.admin.service.syncreport;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.helianhealth.family.he.admin.api.member.param.CustomerAndManualReportParam;
import com.helianhealth.family.he.admin.api.member.param.CustomerArchiveCreateParam;
import com.helianhealth.family.he.admin.api.report.param.ManualFillReportParam;
import com.helianhealth.family.he.admin.api.report.param.ResolveReportItemParam;
import com.helianhealth.family.he.admin.api.report.param.ResolveReportNodeParam;
import com.helianhealth.family.he.admin.api.wgtj.*;
import com.helianhealth.family.he.base.model.Result;
import com.helianhealth.family.he.health.api.report.dto.ManualResolveReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
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


    public static void main(String[] args) throws Exception {
        GwtjReportService service = new GwtjReportService();
        service.syncReport();
    }


    public Result<String> syncReport(String hospitalFid, String hospitalName, String month, String fid, String isPrint,
                                     String userId, String token, String stationId) {
        int size = 0;
        int successCount = 0;
        try {
            List<CustomerAndManualReportParam> result = syncHospitalReport(month, hospitalFid, fid, hospitalName);
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
                        log.error("推送失败, name={}", param.getArchiveCreateParam().getName(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("syncReport error", e);
        }
        return Result.createWithData("success:" + successCount + "/" + size);
    }

    private List<CustomerAndManualReportParam> syncHospitalReport(String month, String hospitalFid, String fid, String hospitalName) throws Exception {
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
                throw new RuntimeException("任务超时未完成 taskId=" + taskId);
            }
            Thread.sleep(1000 * 5);
        }
        String pwd = taskData.getZipPassword();
        List<GwtjRecord> gwtjRecords = this.downloadAndHandleFile(taskId, pwd);
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

            log.info("Header: apitoken=" + accessToken);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getCode();

                System.out.println("响应状态码: " + statusCode);
                System.out.println("响应内容: " + responseBody);

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
                    throw new RuntimeException("查询医院列表失败，状态码: " + statusCode + ", 响应: " + responseBody);
                }
            }
        }
    }

    private List<GwtjRecord> downloadAndHandleFile(String taskId, String pwd) throws Exception {
        String accessToken = TokenManager.getInstance().getValidToken();

        // 仅创建临时 ZIP 文件，不需要解压目录
        Path tempZip = Files.createTempFile("gwtj_", ".zip");
        // 临时解压目录
        Path tempDir = Files.createTempDirectory("gwtj_unzip_");
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
                            } catch (IOException e) {
                                log.error("读取JSON文件失败: {}", jsonFile.getFileName(), e);
                            }
                        });
            }
        } finally {
            // 删除临时解压目录
            if (Files.exists(tempDir)) {
                Files.walk(tempDir)
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
            log.info("临时文件记录名字: {}", tempZip.getFileName());
            Files.deleteIfExists(tempZip);
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
                reportParam.setCheckTime(StrUtil.isBlank(danganInfo.getJiandangRq()) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) : ensureDateTimeFormat(danganInfo.getJiandangRq()));
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
                reportNodeList.removeIf(node -> StrUtil.isEmpty(node.getNodeValue()));
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
        try (InputStream in = GwtjReportService.class.getClassLoader().getResourceAsStream("template_report_null.json")) {
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

    /**
     * @param hunyinZk 2-已婚，3-丧偶，4-离婚
     * @return
     */
    private String buildText(String... labelAndValues) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < labelAndValues.length; i += 2) {
            if (StrUtil.isNotBlank(labelAndValues[i + 1])) {
                sb.append(labelAndValues[i]).append(labelAndValues[i + 1]).append('\n');
            }
        }
        return sb.toString();
    }

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
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
        dateStr = dateStr.trim();
        if (dateStr.length() == 10) {
            return dateStr + " 00:00:00";
        }
        if (dateStr.length() == 19) {
            return dateStr;
        }
        try {
            Date parsed = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(parsed);
        } catch (ParseException e) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
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
                    log.info("建档接口请求失败，状态码: {}, 响应{}", statusCode, responseBody);
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
            // 生成 2020-01 ~ 2026-04 所有月份
            List<String> months = new ArrayList<>();
            LocalDate cursor = LocalDate.of(2019, 10, 1);
            LocalDate end = LocalDate.of(2015, 1, 1);
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
                                month, "HL07731", "2",
                                "1655", "9755af5c058e0293b525cb168ddf49911655", "HL07731"
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