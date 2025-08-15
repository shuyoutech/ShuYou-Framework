package com.shuyoutech.common.core.model;

import com.shuyoutech.common.core.constant.CommonConstants;
import com.shuyoutech.common.core.enums.ErrorCodeEnum;
import com.shuyoutech.common.core.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.slf4j.MDC;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-05 19:43
 **/
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {

    public static final int SUCCESS = ErrorCodeEnum.SUCCESS.getValue();
    public static final int ERROR = ErrorCodeEnum.INTERNAL_SERVER_ERROR.getValue();

    @Schema(description = "状态码")
    protected int code;

    @Schema(description = "响应数据")
    protected T data;

    @Schema(description = "响应消息")
    protected String msg;

    @Schema(description = "链路日志追踪ID")
    private String traceId;

    public static <T> R<T> success() {
        return result(SUCCESS, "", null);
    }

    public static <T> R<T> success(T data) {
        return result(SUCCESS, "", data);
    }

    public static <T> R<T> success(String msg, T data) {
        return result(SUCCESS, msg, data);
    }

    public static <T> R<T> error() {
        return result(ERROR, "", null);
    }

    public static <T> R<T> error(String msg) {
        return result(ERROR, msg, null);
    }

    public static <T> R<T> error(String msgTemplate, Object... params) {
        return error(ERROR, msgTemplate, params);
    }

    public static <T> R<T> error(ErrorCodeEnum code) {
        return result(code.getValue(), code.getLabel(), null);
    }

    public static <T> R<T> error(int code, String msg) {
        return result(code, msg, null);
    }

    public static <T> R<T> error(int code, String msgTemplate, Object... params) {
        String msg = StringUtils.format(msgTemplate, params);
        return result(code, msg, null);
    }

    public static <T> R<T> result(int code, String msg, T data) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        r.setTraceId(MDC.get(CommonConstants.LOG_TRACE));
        return r;
    }

}
