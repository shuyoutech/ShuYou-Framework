package com.shuyoutech.common.web.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.common.core.model.R;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.alibaba.fastjson2.JSONWriter.Feature.WriteMapNullValue;
import static com.shuyoutech.common.core.constant.CommonConstants.CHARSET_UTF_8;
import static com.shuyoutech.common.core.constant.CommonConstants.NO_CACHE;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * @author YangChao
 * @since 2025-04-06 14:50
 **/
@Slf4j
public class JakartaServletUtils extends cn.hutool.extra.servlet.JakartaServletUtil {

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return Objects.requireNonNull(getRequestAttributes()).getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return Objects.requireNonNull(getRequestAttributes()).getResponse();
    }

    private static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, Object> getParamsMap(ServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : getParams(request).entrySet()) {
            params.put(entry.getKey(), ArrayUtil.join(entry.getValue(), StrUtil.COMMA));
        }
        return params;
    }

    /**
     * 返回数据给客户端
     *
     * @param response 响应对象{@link HttpServletResponse}
     * @param code     响应状态码
     * @param msg      响应信息
     */
    public static void write(HttpServletResponse response, int code, String msg) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF_8);

            R<Object> result = R.result(code, msg, null);
            PrintWriter writer = response.getWriter();
            writer.write(JSONObject.toJSONString(result));
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 返回数据给客户端
     *
     * @param response 响应对象{@link HttpServletResponse}
     * @param result   响应数据
     */
    public static void write(HttpServletResponse response, Object result) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF_8);

            PrintWriter writer = response.getWriter();
            writer.write(JSONObject.toJSONString(result));
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void writeError(HttpServletResponse response, int code, String message) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF_8);

            JSONObject errorObject = new JSONObject();
            errorObject.put("code", code);
            errorObject.put("message", message);

            JSONObject object = new JSONObject();
            object.put("error", errorObject);

            PrintWriter writer = response.getWriter();
            writer.write(object.toJSONString());
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 返回数据给客户端
     *
     * @param response 响应对象{@link HttpServletResponse}
     */
    public static <T> void writeStartEvent(HttpServletResponse response) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setCharacterEncoding(CHARSET_UTF_8);
            response.setContentType(TEXT_EVENT_STREAM_VALUE);
            response.setHeader(CACHE_CONTROL, NO_CACHE);

            PrintWriter writer = response.getWriter();
            writer.write("event:start");
            writer.println();
            writer.write("data: ");
            writer.println();
            writer.println();
            writer.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 返回数据给客户端
     *
     * @param response 响应对象{@link HttpServletResponse}
     * @param data     响应数据
     */
    public static <T> void writeAnswerEvent(HttpServletResponse response, String data) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setCharacterEncoding(CHARSET_UTF_8);
            response.setContentType(TEXT_EVENT_STREAM_VALUE);
            response.setHeader(CACHE_CONTROL, NO_CACHE);

            JSONObject json = new JSONObject();
            json.put("content", data);

            PrintWriter writer = response.getWriter();
            writer.write("event:answer");
            writer.println();
            writer.write("data: " + json.toJSONString(WriteMapNullValue));
            writer.println();
            writer.println();
            writer.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 返回数据给客户端
     *
     * @param data 响应数据
     */
    public static <T> void writeThinkingEvent(PrintWriter printWriter, String data) {
        try {
            JSONObject json = new JSONObject();
            json.put("content", data);

            printWriter.write("event:reasoning");
            printWriter.println();
            printWriter.write("data: " + json.toJSONString(WriteMapNullValue));
            printWriter.println();
            printWriter.println();
            printWriter.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 返回数据给客户端
     *
     * @param data 响应数据
     */
    public static <T> void writeAnswerEvent(PrintWriter printWriter, String data) {
        try {
            JSONObject json = new JSONObject();
            json.put("content", data);

            printWriter.write("event:answer");
            printWriter.println();
            printWriter.write("data: " + json.toJSONString(WriteMapNullValue));
            printWriter.println();
            printWriter.println();
            printWriter.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 返回数据给客户端
     *
     * @param response 响应对象{@link HttpServletResponse}
     * @param data     响应数据
     */
    public static <T> void writeEndEvent(HttpServletResponse response, Object data) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setCharacterEncoding(CHARSET_UTF_8);
            response.setContentType(TEXT_EVENT_STREAM_VALUE);
            response.setHeader(CACHE_CONTROL, NO_CACHE);

            PrintWriter writer = response.getWriter();
            writer.write("event:end");
            writer.println();
            writer.write("data: " + JSONObject.toJSONString(data));
            writer.println();
            writer.println();
            writer.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 返回数据给客户端
     *
     * @param data 响应数据
     */
    public static <T> void writeEndEvent(PrintWriter printWriter, Object data) {
        try {
            printWriter.write("event:end");
            printWriter.println();
            printWriter.write("data: " + JSONObject.toJSONString(data));
            printWriter.println();
            printWriter.println();
            printWriter.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 返回数据给客户端
     *
     * @param response 响应对象{@link HttpServletResponse}
     * @param data     响应数据
     */
    public static <T> void writeErrorEvent(HttpServletResponse response, Object data) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setCharacterEncoding(CHARSET_UTF_8);
            response.setContentType(TEXT_EVENT_STREAM_VALUE);
            response.setHeader(CACHE_CONTROL, NO_CACHE);

            PrintWriter writer = response.getWriter();
            writer.write("event:error");
            writer.println();
            writer.write("data: " + JSONObject.toJSONString(data));
            writer.println();
            writer.println();
            writer.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 返回数据给客户端
     *
     * @param data 响应数据
     */
    public static <T> void writeErrorEvent(PrintWriter printWriter, Object data) {
        try {
            printWriter.write("event:error");
            printWriter.println();
            printWriter.write("data: " + JSONObject.toJSONString(data));
            printWriter.println();
            printWriter.println();
            printWriter.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 返回数据给客户端
     *
     * @param data 响应数据
     */
    public static void write(PrintWriter printWriter, Object data) {
        try {
            printWriter.write(JSONObject.toJSONString(data));
            printWriter.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
