package com.helianhealth.family.he.admin.service.syncreport;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class HttpsClientUtils {

    // 证书配置
    private static final String P12_PATH = "03_浏览器证书.p12";
    private static final String CERT_PASSWORD = "cces2018";
    private static final String KEYSTORE_TYPE = "PKCS12";
    // 加载客户端证书
    public static CloseableHttpClient createHttpsClient() throws Exception {
        // 加载客户端证书
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);

        // 尝试从多个位置加载证书文件
        InputStream inputStream = null;
        File certFile = null;

        // 1. 尝试从resources目录加载（打包后）
        inputStream = HttpsClientUtils.class.getResourceAsStream("/" + P12_PATH);
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

        System.out.println("成功加载证书文件: " + (certFile != null ? certFile.getAbsolutePath() : "classpath:" + P12_PATH));

        try {
            keyStore.load(inputStream, CERT_PASSWORD.toCharArray());
        } finally {
            inputStream.close();
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
}
