package com.shuyoutech.common.web.interceptor;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.shuyoutech.common.core.constant.CommonConstants;
import com.shuyoutech.common.core.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 *
 * @author YangChao
 * @date 2023-07-20 17:26
 **/
@Slf4j
public class TraceIdInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String traceId = JakartaServletUtil.getHeader(request, CommonConstants.HEADER_REQUEST_TRACE_ID, StandardCharsets.UTF_8);
        if (StringUtils.isBlank(traceId)) {
            return true;
        }
        MDC.put(CommonConstants.LOG_TRACE, traceId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(CommonConstants.LOG_TRACE);
    }

}
