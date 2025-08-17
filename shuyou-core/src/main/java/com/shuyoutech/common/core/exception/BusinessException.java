package com.shuyoutech.common.core.exception;

import com.shuyoutech.common.core.enums.ErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author YangChao
 * @date 2025-07-05 20:37
 **/
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

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

    public BusinessException(ErrorCodeEnum resultCode) {
        super(resultCode.getLabel());
        this.code = resultCode.getValue();
        this.message = resultCode.getLabel();
    }

    public BusinessException(String message) {
        this(ErrorCodeEnum.INTERNAL_SERVER_ERROR.getValue(), message);
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "BusinessException [code=" + getCode() + ", message=" + getMessage() + "]";
    }

}
