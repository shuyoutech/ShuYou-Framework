package com.shuyoutech.common.core.constant;

/**
 * 公共的常量类
 *
 * @author YangChao
 * @date 2025-08-15 12:23
 **/
public interface CommonConstants {

    /**
     * 项目前缀
     */
    String PROJECT_PREFIX = "shuyou";

    /**
     * 授权信息字段
     */
    String HEADER_TOKEN = "satoken";

    /**
     * 令牌自定义标识
     */
    String HEADER_AUTHORIZATION = "Authorization";

    /**
     * 令牌前缀
     */
    String HEADER_AUTHORIZATION_PREFIX = "Bearer ";

    /**
     * Header 用户ID字段
     */
    String HEADER_USER_ID = "User-Id";

    /**
     * Header 用户名字段
     */
    String HEADER_USER_NAME = "User-Name";

    /**
     * Header 用户字段
     */
    String HEADER_USER_INFO = "User-Info";

    /**
     * 应用ID
     */
    String HEADER_APP_ID = "App-Id";

    /**
     * 应用摘要HASH
     */
    String HEADER_DIGEST_HASH = "Digest-Hash";

    /**
     * 应用摘要时间
     */
    String HEADER_DIGEST_TIME = "Digest-Time";

    /**
     * 失效时间 5分钟
     */
    Integer HEADER_DIGEST_EXPIRE_TIME = 300000;

    /**
     * 请求Id 日志唯一检索id
     */
    String HEADER_REQUEST_ID = "Request-Id";

    /**
     * header Access-Control-Expose
     */
    String HEADER_ACCESS_CONTROL_EXPOSE = "Access-Control-Expose-Headers";

    /**
     * header Content-Disposition
     */
    String HEADER_CONTENT_DISPOSITION = "Content-Disposition";

    /**
     * 过期时间 1天 = 24 * 60 * 60 * 1000
     */
    long EXPIRE_TIME = 86400000L;

    /**
     * 请求Id 日志唯一检索id
     */
    String HEADER_REQUEST_TRACE_ID = "Request-Trace-Id";

    /**
     * 日志追踪ID log trace
     */
    String LOG_TRACE = "trace";

    /**
     * redis缓存token key
     */
    String CACHE_SECURITY_TOKEN_KEY = "SECURITY:TOKEN:";

    /**
     * 用户ID字段
     */
    String USER_ID = "user_id";

    /**
     * 用户名字段
     */
    String USER_NAME = "user_name";

    /**
     * 用户类型字段
     */
    String USER_TYPE = "user_type";

    /**
     * 编码 ISO-8859-1
     */
    String CHARSET_ISO_8859_1 = "ISO-8859-1";

    /**
     * 编码 UTF-8
     */
    String CHARSET_UTF_8 = "UTF-8";

    /**
     * 编码 GBK
     */
    String CHARSET_GBK = "GBK";

    /**
     * 客户端识别客户使用的操作系统及版本、CPU类型、浏览器及版本、浏览器渲染引擎、浏览器语言、浏览器插件等
     */
    String USER_AGENT = "User-Agent";

    /**
     * 火狐
     */
    String FIRE_FOX = "firefox";

    /**
     * Cache-Control
     */
    String CACHE_CONTROL = "Cache-Control";

    /**
     * NO_CACHE
     */
    String NO_CACHE = "no-cache";

}
