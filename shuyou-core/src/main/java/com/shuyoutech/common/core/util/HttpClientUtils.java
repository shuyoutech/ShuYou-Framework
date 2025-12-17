package com.shuyoutech.common.core.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.common.core.constant.StringConstants;
import com.shuyoutech.common.core.enums.ErrorCodeEnum;
import com.shuyoutech.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.TimeValue;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP 客户端工具类
 * <p>
 * 基于Apache HttpClient 5.x实现，提供统一的HTTP请求封装
 * <p>
 * 特性：
 * <ul>
 *     <li>单例模式：使用静态内部类实现线程安全的单例</li>
 *     <li>连接池管理：支持连接复用，最大连接数200，每个路由最大20</li>
 *     <li>自动重试：失败自动重试3次，间隔1秒</li>
 *     <li>超时控制：连接请求超时300秒，响应超时300秒</li>
 *     <li>Cookie管理：支持Cookie自动管理</li>
 * </ul>
 * <p>
 * 支持的操作：
 * <ul>
 *     <li>GET请求：支持URL参数和对象返回</li>
 *     <li>POST请求：支持JSON格式数据</li>
 *     <li>PUT请求：支持JSON格式数据</li>
 *     <li>文件上传：支持multipart/form-data格式</li>
 *     <li>文件下载：支持下载文件到本地</li>
 * </ul>
 *
 * @author YangChao
 * @date 2025-07-06 14:40
 */
@Slf4j
public class HttpClientUtils {

    /**
     * 单例持有者（静态内部类）
     * <p>
     * 采用静态内部类实现单例模式，具有以下优点：
     * <ul>
     *     <li>延迟加载：只有在调用getInstance()时才会加载</li>
     *     <li>线程安全：由JVM保证静态初始化器的线程安全性</li>
     *     <li>无需同步：不需要额外的同步机制</li>
     * </ul>
     */
    private static class SingletonHolder {

        /**
         * HTTP客户端实例 静态初始化器，由JVM保证线程安全
         */
        private static CloseableHttpClient httpclient;

        static {
            try {
                // 配置请求参数：连接保活120秒，连接请求超时120秒，响应超时120秒
                RequestConfig defaultRequestConfig = RequestConfig.custom()//
                        .setConnectionKeepAlive(TimeValue.ofSeconds(120))//
                        .setConnectionRequestTimeout(120, TimeUnit.SECONDS)//
                        .setResponseTimeout(120, TimeUnit.SECONDS)//
                        .build();

                // 创建 Cookie 存储
                BasicCookieStore defaultCookieStore = new BasicCookieStore();

                // 配置连接池：最大连接数200，每个路由最大20个连接
                PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
                connectionManager.setMaxTotal(200);
                connectionManager.setDefaultMaxPerRoute(20);

                // 构建 HTTP 客户端
                httpclient = HttpClients.custom()//
                        .setDefaultCookieStore(defaultCookieStore)//
                        .setDefaultRequestConfig(defaultRequestConfig)//
                        .setConnectionManager(connectionManager)//
                        .setRetryStrategy(new DefaultHttpRequestRetryStrategy(3, TimeValue.of(1, TimeUnit.SECONDS)))//
                        .evictExpiredConnections()//
                        .build();
            } catch (Exception exception) {
                log.error(exception.getMessage(), exception);
            }
        }
    }

    /**
     * 获取 HTTP 客户端单例实例
     */
    public static CloseableHttpClient getInstance() {
        CloseableHttpClient client = SingletonHolder.httpclient;
        if (client == null) {
            throw new IllegalStateException("HttpClient未初始化，请检查配置");
        }
        return client;
    }

    /**
     * URL 参数编码
     * <p>
     * 将Map参数编码为URL查询字符串格式（key1=value1&key2=value2）
     * 空值会被编码为 key= 的形式
     *
     * @param paramsMap 参数
     * @return 编码后的 URL参数字符串
     */
    public static String encodeParameters(Map<String, Object> paramsMap) {
        if (MapUtils.isEmpty(paramsMap)) {
            return "";
        }
        List<String> list = CollectionUtils.newArrayList();
        for (String key : paramsMap.keySet()) {
            if (ObjectUtils.isEmpty(paramsMap.get(key))) {
                list.add(URLEncoder.encode(key, StandardCharsets.UTF_8) + StringConstants.EQUAL + StringConstants.EMPTY);
            } else {
                list.add(URLEncoder.encode(key, StandardCharsets.UTF_8) + StringConstants.EQUAL + URLEncoder.encode(String.valueOf(paramsMap.get(key)), StandardCharsets.UTF_8));
            }
        }
        return CollectionUtils.join(list, StringConstants.AMPERSAND);
    }

    /**
     * 组装完整URL（包含查询参数）
     *
     * @param url      基础 URL地址
     * @param paramMap 请求参数
     * @return 完整的URL字符串（包含查询参数）
     */
    public static String packageUrl(String url, Map<String, Object> paramMap) {
        if (MapUtils.isEmpty(paramMap)) {
            return url;
        }
        return url + StringConstants.QUESTION + encodeParameters(paramMap);
    }

    /**
     * 发送GET请求（返回字符串）
     *
     * @param url 请求地址
     * @return 响应体字符串
     * @throws BusinessException 请求失败时抛出
     */
    public static String sendGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        return execute(httpGet);
    }

    /**
     * 发送GET请求（带参数，返回字符串）
     *
     * @param url      请求地址
     * @param paramMap 请求参数
     * @return 响应体字符串
     * @throws BusinessException 请求失败时抛出
     */
    public static String sendGet(String url, Map<String, Object> paramMap) {
        url = packageUrl(url, paramMap);
        HttpGet httpGet = new HttpGet(url);
        return execute(httpGet);
    }

    /**
     * 发送GET请求（返回指定类型对象）
     * <p>
     * 响应体会被解析为 JSON 并转换为指定类型
     *
     * @param url          请求地址
     * @param responseType 响应类型
     * @param <T>          返回类型
     * @return 解析后的对象，响应为空时返回null
     * @throws BusinessException 请求失败时抛出
     */
    public static <T> T sendGet(String url, Class<T> responseType) {
        String body = sendGet(url);
        if (StringUtils.isBlank(body)) {
            return null;
        }
        return JSONObject.parseObject(body, responseType);
    }

    /**
     * 发送GET请求（带参数，返回指定类型对象）
     * <p>
     * 响应体会被解析为 JSON 并转换为指定类型
     *
     * @param url          请求地址
     * @param paramMap     请求参数
     * @param responseType 响应类型
     * @param <T>          返回类型
     * @return 解析后的对象，响应为空时返回null
     * @throws BusinessException 请求失败时抛出
     */
    public static <T> T sendGet(String url, Map<String, Object> paramMap, Class<T> responseType) {
        url = packageUrl(url, paramMap);
        String body = sendGet(url);
        if (StringUtils.isBlank(body)) {
            return null;
        }
        return JSONObject.parseObject(body, responseType);
    }

    /**
     * 发送 GET 请求下载文件
     * <p>
     * 使用较长的超时时间（5分钟），适合大文件下载
     *
     * @param url      文件下载地址
     * @param destFile 目标文件路径
     * @return 下载的文件对象，失败返回null
     */
    public static File sendGetFile(String url, String destFile) {
        // 文件下载使用更长的超时时间：连接保活5分钟，连接请求超时5分钟，响应超时5分钟
        RequestConfig requestConfig = RequestConfig.custom()//
                .setConnectionKeepAlive(TimeValue.ofMinutes(5))//
                .setConnectionRequestTimeout(5, TimeUnit.MINUTES)//
                .setResponseTimeout(5, TimeUnit.MINUTES)//
                .build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        byte[] bytes = executeToByteArray(httpGet);
        if (null == bytes) {
            return null;
        }
        return FileUtils.writeBytes(bytes, destFile);
    }

    /**
     * 发送POST请求（返回指定类型对象）
     * <p>
     * 请求体为JSON格式，响应体会被解析为JSON并转换为指定类型
     *
     * @param url          请求地址
     * @param paramMap     请求参数Map（会被序列化为JSON）
     * @param responseType 响应类型
     * @param <T>          返回类型
     * @return 解析后的对象
     * @throws BusinessException 请求失败时抛出
     */
    public static <T> T sendPost(String url, Map<String, Object> paramMap, Class<T> responseType) {
        String result = sendPost(url, paramMap);
        return JSON.parseObject(result, responseType);
    }

    /**
     * 发送POST请求（返回字符串）
     * <p>
     * 请求体为JSON格式（Content-Type: application/json）
     *
     * @param url      请求地址
     * @param paramMap 请求参数Map（会被序列化为JSON）
     * @return 响应体字符串
     * @throws BusinessException 请求失败时抛出
     */
    public static String sendPost(String url, Map<String, Object> paramMap) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(JSON.toJSONString(paramMap), ContentType.APPLICATION_JSON));
        return execute(httpPost);
    }

    /**
     * 发送PUT请求（返回指定类型对象）
     * <p>
     * 请求体为JSON格式，响应体会被解析为JSON并转换为指定类型
     *
     * @param url          请求地址
     * @param paramMap     请求参数Map（会被序列化为JSON）
     * @param responseType 响应类型
     * @param <T>          返回类型
     * @return 解析后的对象
     * @throws BusinessException 请求失败时抛出
     */
    public static <T> T sendPut(String url, Map<String, Object> paramMap, Class<T> responseType) {
        String result = sendPut(url, paramMap);
        return JSON.parseObject(result, responseType);
    }

    /**
     * 发送PUT请求（返回字符串）
     * <p>
     * 请求体为JSON格式（Content-Type: application/json）
     *
     * @param url      请求地址
     * @param paramMap 请求参数Map（会被序列化为JSON）
     * @return 响应体字符串
     * @throws BusinessException 请求失败时抛出
     */
    public static String sendPut(String url, Map<String, Object> paramMap) {
        HttpPut httpPut = new HttpPut(url);
        httpPut.setEntity(new StringEntity(JSON.toJSONString(paramMap), ContentType.APPLICATION_JSON));
        return execute(httpPut);
    }

    /**
     * 上传文件
     * <p>
     * 使用multipart/form-data格式上传文件，支持同时上传文件和文本参数
     * 使用较长的超时时间（5分钟），适合大文件上传
     *
     * @param url    请求地址
     * @param params 文本参数Map（可选）
     * @param file   要上传的文件（可选）
     * @return 响应体字符串
     * @throws BusinessException 请求失败时抛出
     */
    public static String sendFile(String url, Map<String, String> params, File file) {
        // 文件上传使用更长的超时时间：连接保活5分钟，连接请求超时5分钟，响应超时5分钟
        RequestConfig requestConfig = RequestConfig.custom()//
                .setConnectionKeepAlive(TimeValue.ofMinutes(5))//
                .setConnectionRequestTimeout(5, TimeUnit.MINUTES)//
                .setResponseTimeout(5, TimeUnit.MINUTES)//
                .build();
        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);

        // 构建multipart/form-data请求体
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        if (file != null) {
            FileBody bin = new FileBody(file);
            builder.addPart("file", bin);
        }
        if (params != null) {
            for (String key : params.keySet()) {
                builder.addPart(key, new StringBody(params.get(key), ContentType.create("text/plain", StandardCharsets.UTF_8)));
            }
        }
        HttpEntity reqEntity = builder.build();
        post.setEntity(reqEntity);
        return execute(post);
    }

    /**
     * 执行HTTP请求（返回字符串）
     * <p>
     * 通用请求执行方法，自动处理响应实体转换
     *
     * @param request HTTP 请求对象
     * @return 响应体字符串
     * @throws BusinessException 请求失败或响应实体为空时抛出
     */
    public static String execute(HttpUriRequest request) {
        try {
            CloseableHttpClient httpClient = getInstance();
            return httpClient.execute(request, response -> {
                if (response.getEntity() == null) {
                    throw new BusinessException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
                }
                return EntityUtils.toString(response.getEntity());
            });
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new BusinessException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 执行HTTP请求（返回字节数组）
     * <p>
     * 用于下载文件等需要获取二进制数据的场景
     *
     * @param request HTTP 请求对象
     * @return 响应体字节数组，失败返回null
     */
    public static byte[] executeToByteArray(HttpUriRequest request) {
        try {
            CloseableHttpClient httpClient = getInstance();
            return httpClient.execute(request, response -> {
                if (response.getEntity() == null) {
                    throw new BusinessException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
                }
                return EntityUtils.toByteArray(response.getEntity());
            });
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
        return null;
    }

    /**
     * 设置 HTTP 请求头
     * <p>
     * 使用示例：
     * <pre>{@code
     * List<Header> headers = List.of(
     *     new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
     *     new BasicHeader(HttpHeaders.ACCEPT, "application/json")
     * );
     * HttpClientUtils.setHeaders(request, headers);
     * }</pre>
     *
     * @param request HTTP 请求对象
     * @param headers 请求头集合
     */
    public static void setHeaders(HttpUriRequest request, List<Header> headers) {
        if (CollectionUtils.isEmpty(headers)) {
            return;
        }
        for (Header header : headers) {
            request.addHeader(header);
        }
    }

}
