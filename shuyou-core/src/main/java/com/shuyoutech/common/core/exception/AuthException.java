package com.shuyoutech.common.core.exception;

import com.shuyoutech.common.core.enums.ErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author YangChao
 * @date 2025-07-10 10:50
 **/
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuthException extends RuntimeException {

    /**
     * 全局错误码
     *
     * @see ErrorCodeEnum
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    public AuthException(ErrorCodeEnum resultCode) {
        super(resultCode.getLabel());
        this.code = resultCode.getValue();
        this.message = resultCode.getLabel();
    }

    public AuthException(String message) {
        this(ErrorCodeEnum.ACCESS_DENIED.getValue(), message);
    }

    public AuthException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

}
