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
 * @author YangChao
 * @date 2025-07-06 14:40
 **/
@Slf4j
public class HttpClientUtils {

    /**
     * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例 没有绑定关系，而且只有调用才会装载，从而实现延迟加载
     */
    private static class SingletonHolder {

        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static CloseableHttpClient httpclient;

        static {
            try {
                RequestConfig defaultRequestConfig = RequestConfig.custom() //
                        .setConnectionKeepAlive(TimeValue.ofSeconds(300)) //
                        .setConnectionRequestTimeout(300, TimeUnit.SECONDS) //
                        .setResponseTimeout(300, TimeUnit.SECONDS) //
                        .build();
                BasicCookieStore defaultCookieStore = new BasicCookieStore();

                PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
                connectionManager.setMaxTotal(200);
                connectionManager.setDefaultMaxPerRoute(20);

                httpclient = HttpClients.custom().setDefaultCookieStore(defaultCookieStore).setDefaultRequestConfig(defaultRequestConfig)//
                        .setConnectionManager(connectionManager)//
                        .setRetryStrategy(new DefaultHttpRequestRetryStrategy(3, TimeValue.of(1, TimeUnit.SECONDS)))//
                        .evictExpiredConnections().build();
            } catch (Exception e) {
                log.error("init ==================== exception:{}", e.getMessage());
            }
        }
    }

    /**
     * 获取CloseableHttpClient单例
     */
    public synchronized static CloseableHttpClient getInstance() {
        return SingletonHolder.httpclient;
    }

    /**
     * 上送参数组装
     *
     * @param paramsMap 上送参数Map
     * @return url
     */
    public static String encodeParameters(Map<String, Object> paramsMap) {
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
     * 组装url
     *
     * @param url      请求地址
     * @param paramMap 请求参数
     * @return String
     */
    public static String packageUrl(String url, Map<String, Object> paramMap) {
        if (MapUtils.isEmpty(paramMap)) {
            return url;
        }
        return url + StringConstants.QUESTION + encodeParameters(paramMap);
    }

    /**
     * 发送get请求
     *
     * @param url 请求地址
     * @return 返回对象
     */
    public static String sendGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        return execute(httpGet);
    }

    /**
     * 发送get请求
     *
     * @param url      请求地址
     * @param paramMap 请求参数
     * @return 返回对象
     */
    public static String sendGet(String url, Map<String, Object> paramMap) {
        url = packageUrl(url, paramMap);
        HttpGet httpGet = new HttpGet(url);
        return execute(httpGet);
    }

    /**
     * 发送get请求
     *
     * @param url 请求地址
     * @return 返回对象
     */
    public static <T> T sendGet(String url, Class<T> responseType) {
        String body = sendGet(url);
        if (StringUtils.isBlank(body)) {
            return null;
        }
        return JSONObject.parseObject(body, responseType);
    }

    /**
     * 发送get请求
     *
     * @param url      请求地址
     * @param paramMap 请求参数
     * @return 返回对象
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
     * 发送get请求获取文件
     *
     * @param url 请求地址
     * @return 返回对象
     */
    public static File sendGetFile(String url, String destFile) {
        RequestConfig requestConfig = RequestConfig.custom() //
                .setConnectionKeepAlive(TimeValue.ofMinutes(5)) //
                .setConnectionRequestTimeout(5, TimeUnit.MINUTES) //
                .setResponseTimeout(5, TimeUnit.MINUTES) //
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
     * post 请求
     *
     * @param url          请求地址
     * @param paramMap     请求参数
     * @param responseType 返回类型
     * @return 返回对象
     */
    public static <T> T sendPost(String url, Map<String, Object> paramMap, Class<T> responseType) {
        String result = sendPost(url, paramMap);
        return JSON.parseObject(result, responseType);
    }

    /**
     * 发送post请求
     *
     * @param url      请求地址
     * @param paramMap 请求参数
     * @return 返回对象
     */
    public static String sendPost(String url, Map<String, Object> paramMap) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(JSON.toJSONString(paramMap), ContentType.APPLICATION_JSON));
        return execute(httpPost);
    }

    /**
     * 发送put请求
     *
     * @param url      请求地址
     * @param paramMap 请求参数
     * @return 返回对象
     */
    public static <T> T sendPut(String url, Map<String, Object> paramMap, Class<T> responseType) {
        String result = sendPost(url, paramMap);
        return JSON.parseObject(result, responseType);
    }

    /**
     * 发送put请求
     *
     * @param url      请求地址
     * @param paramMap 请求参数
     * @return 返回对象
     */
    public static String sendPut(String url, Map<String, Object> paramMap) {
        HttpPut httpPut = new HttpPut(url);
        httpPut.setEntity(new StringEntity(JSON.toJSONString(paramMap), ContentType.APPLICATION_JSON));
        return execute(httpPut);
    }

    /**
     * 上传文件
     *
     * @param url   请求地址
     * @param param 请求数据对象
     * @param file  文件
     * @return 返回对象
     */
    public static String sendFile(String url, Map<String, String> param, File file) {
        RequestConfig requestConfig = RequestConfig.custom() //
                .setConnectionKeepAlive(TimeValue.ofMinutes(5)) //
                .setConnectionRequestTimeout(5, TimeUnit.MINUTES) //
                .setResponseTimeout(5, TimeUnit.MINUTES)//
                .build();
        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        if (file != null) {
            FileBody bin = new FileBody(file);
            builder.addPart("file", bin);
        }
        for (String key : param.keySet()) {
            builder.addPart(key, new StringBody(param.get(key), ContentType.create("text/plain", StandardCharsets.UTF_8)));
        }
        HttpEntity reqEntity = builder.build();
        post.setEntity(reqEntity);
        return execute(post);
    }

    /**
     * 执行http请求
     *
     * @param request 请求对象
     * @return T
     */
    public static String execute(HttpUriRequest request) {
        try {
            CloseableHttpClient httpClient = getInstance();
            return httpClient.execute(request, response -> {
                if (response.getEntity() == null) {
                    log.error("execute ==================== code:{}", response.getCode());
                    throw new BusinessException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
                }
                return EntityUtils.toString(response.getEntity());
            });
        } catch (Exception exception) {
            log.error("execute ==================== exception:{}", exception.getMessage());
            throw new BusinessException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 执行http请求
     *
     * @param request 请求对象
     * @return T
     */
    public static byte[] executeToByteArray(HttpUriRequest request) {
        try {
            CloseableHttpClient httpClient = getInstance();
            return httpClient.execute(request, response -> {
                if (response.getEntity() == null) {
                    log.error("executeToByteArray ==================== code:{}", response.getCode());
                    throw new BusinessException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
                }
                return EntityUtils.toByteArray(response.getEntity());
            });
        } catch (Exception exception) {
            log.error("executeToByteArray ==================== exception:{}", exception.getMessage());
        }
        return null;
    }

    /**
     * 设置请求头
     * List<Header> headers = List.of(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"), new BasicHeader(HttpHeaders.ACCEPT, "application/json"));
     *
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
