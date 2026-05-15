package com.helianhealth.family.he.admin.service.syncreport;

import com.google.gson.Gson;
import com.helianhealth.family.he.admin.api.wgtj.TokenData;
import com.helianhealth.family.he.admin.api.wgtj.TokenResponse;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class TokenManager {
    private static final Logger log = LoggerFactory.getLogger(TokenManager.class);
    private static final String SERVER_URL = "https://gwjkfw.jljcws.com:8443/hcclyq";
    private static final String TOKEN_URL = SERVER_URL + "/api/v1/auth/token";
    private static final String APP_KEY = "18288q02310001hHizEWIKITLNZ4pXby1nMZ";
    private static final String APP_SECRET = "qu9eh2njn85ejo285jiqwrsx5y74";

    // 证书配置
    private static final String P12_PATH = "03_浏览器证书.p12";
    private static final String CERT_PASSWORD = "cces2018";
    private static final String KEYSTORE_TYPE = "PKCS12";
    private static final Gson GSON = new Gson();

    private static volatile TokenManager instance;

    private volatile String accessToken;
    private volatile long expiresAt;           // token绝对过期时间（epoch毫秒）
    private volatile long lastRefreshTime;     // 上次刷新时间

    // 提前5分钟刷新，避免临界过期
    private static final long REFRESH_BUFFER_MS = 5 * 60 * 1000;
    // epoch阈值：小于此值视为相对时长（6h = 21600000ms）
    private static final long EPOCH_THRESHOLD = 1609459200000L; // 2021-01-01

    private TokenManager() {
    }

    public static TokenManager getInstance() {
        if (instance == null) {
            synchronized (TokenManager.class) {
                if (instance == null) {
                    instance = new TokenManager();
                }
            }
        }
        return instance;
    }

    /**
     * 获取有效的accessToken，过期或即将过期时自动刷新
     * 其他接口直接调用此方法即可，无需关心token生命周期
     */
    public String getValidToken() throws Exception {
        if (isTokenValid()) {
            return accessToken;
        }
        synchronized (this) {
            if (isTokenValid()) {
                return accessToken;
            }
            refreshToken();
        }
        return accessToken;
    }

    private boolean isTokenValid() {
        if (accessToken == null || accessToken.isEmpty()) {
            return false;
        }
        return System.currentTimeMillis() < (expiresAt - REFRESH_BUFFER_MS);
    }

    private void refreshToken() throws Exception {
        log.info("\n>>> Token已过期或即将过期，正在自动刷新...");
        TokenResponse response = getToken();
        if (response == null || response.getData() == null) {
            throw new RuntimeException("刷新Token失败：响应中无data");
        }

        TokenData tokenData = response.getData();
        this.accessToken = tokenData.getAccessToken();
        this.lastRefreshTime = System.currentTimeMillis();

        // expireTime可能是绝对时间戳或相对时长，统一处理
        Long rawExpireTime = tokenData.getExpireTime();
        if (rawExpireTime != null) {
            if (rawExpireTime > EPOCH_THRESHOLD) {
                // 绝对时间戳
                this.expiresAt = rawExpireTime;
            } else {
                // 相对时长（毫秒），从当前时间计算
                this.expiresAt = this.lastRefreshTime + rawExpireTime;
            }
        } else {
            // 无过期时间字段，默认按6小时处理
            this.expiresAt = this.lastRefreshTime + 6 * 60 * 60 * 1000;
        }

        log.info(">>> Token刷新成功");
        log.info("    AccessToken: {}", this.accessToken);
        log.info("    有效期至: {}", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(this.expiresAt)));
    }
    public static TokenResponse getToken() throws Exception {
        // 创建支持双向SSL认证的HttpClient
        try (CloseableHttpClient httpClient = createHttpsClient()) {
            HttpPost httpPost = new HttpPost(TOKEN_URL);

            // 构建请求参数
            String params = String.format("appKey=%s&appSecret=%s", APP_KEY, APP_SECRET);
            httpPost.setEntity(new StringEntity(params, ContentType.APPLICATION_FORM_URLENCODED));

            log.info("========== 发送Token请求 ==========");
            log.info("URL: {}", TOKEN_URL);
            log.info("参数: appKey={}", APP_KEY);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getCode();

                log.info("响应状态码: {}", statusCode);
                log.info("响应内容: {}", responseBody);

                if (statusCode == 200) {
                    return GSON.fromJson(responseBody, TokenResponse.class);
                } else {
                    throw new RuntimeException("获取Token失败，状态码: " + statusCode + ", 响应: " + responseBody);
                }
            }
        }
    }
    private static CloseableHttpClient createHttpsClient() throws Exception {
        // 加载客户端证书
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);

        // 尝试从多个位置加载证书文件
        InputStream inputStream = null;
        File certFile = null;

        // 1. 尝试从resources目录加载（打包后）
        inputStream = TokenManager.class.getResourceAsStream("/" + P12_PATH);
        if (inputStream == null) {
            // 2. 尝试从当前目录加载
            certFile = new File(P12_PATH);
            if (certFile.exists()) {
                inputStream = new FileInputStream(certFile);
            } else {
                // 3. 尝试从src/main/resources加载（开发时）
                certFile = new File("src/main/resources/" + P12_PATH);
                if (certFile.exists()) {
                    inputStream = new FileInputStream(certFile);
                }
            }
        }

        if (inputStream == null) {
            throw new RuntimeException("无法找到证书文件: " + P12_PATH +
                    "\n请确保证书文件位于以下任一位置:\n" +
                    "  - src/main/resources/" + P12_PATH + "\n" +
                    "  - 当前目录/" + P12_PATH + "\n" +
                    "  - classpath根目录/" + P12_PATH);
        }

        log.info("成功加载证书文件: {}", (certFile != null ? certFile.getAbsolutePath() : "classpath:" + P12_PATH));

        try {
            keyStore.load(inputStream, CERT_PASSWORD.toCharArray());
        } finally {
            log.info("关闭证书文件输入流 {}", inputStream);
        }

        // 创建SSLContext，信任自签名证书
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, CERT_PASSWORD.toCharArray())  // 加载客户端证书
                .loadTrustMaterial((chain, authType) -> true)           // 信任所有证书（仅用于开发/测试）
                .build();

        // 创建SSL连接工厂
        SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .build();

        // 创建连接管理器
        PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory);

        // 连接超时10s，响应超时60s
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(10))
                .setResponseTimeout(Timeout.ofSeconds(60))
                .build();

        // 创建HttpClient
        return HttpClients.custom()
                .setConnectionManager(connectionManagerBuilder.build())
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
    public long getExpiresAt() {
        return expiresAt;
    }

    public long getLastRefreshTime() {
        return lastRefreshTime;
    }
}