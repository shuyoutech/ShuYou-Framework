package com.shuyoutech.common.web.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.common.core.model.R;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author YangChao
 * @date 2025-04-06 14:50
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
        ServletOutputStream outputStream = null;
        try {
            R<Object> result = R.result(code, msg, null);
            String message = JSONObject.toJSONString(result);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=UTF-8");
            outputStream = response.getOutputStream();
            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
            response.flushBuffer();
        } catch (IOException e) {
            log.error("write ============= exception:{}", e.getMessage());
        } finally {
            IoUtil.close(outputStream);
        }
    }

}
