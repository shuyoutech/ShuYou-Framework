package com.shuyoutech.common.web.handler;

import com.alibaba.fastjson2.JSONException;
import com.shuyoutech.common.core.constant.StringConstants;
import com.shuyoutech.common.core.enums.ErrorCodeEnum;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.model.R;
import com.shuyoutech.common.core.util.CollectionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLSyntaxErrorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 全局的的异常拦截器
 *
 * @author YangChao
 * @date 2025-04-06 16:27
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    public <T> R<T> handleException(BusinessException e, HttpServletRequest request) {
        log.error("BusinessException", e);
        return R.error(e.getCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BindException.class)
    public <T> R<T> processException(BindException e) {
        log.error("BindException", e);
        String message = ErrorCodeEnum.ILLEGAL_ARGUMENT_EXCEPTION.getLabel();
        if (CollectionUtils.isNotEmpty(e.getAllErrors())) {
            message = CollectionUtils.join(e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()), StringConstants.SEMICOLON);
        }
        return R.error(ErrorCodeEnum.ILLEGAL_ARGUMENT_EXCEPTION.getValue(), message);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ConstraintViolationException.class)
    public <T> R<T> processException(ConstraintViolationException e) {
        log.error("ConstraintViolationException", e);
        String message = ErrorCodeEnum.ILLEGAL_ARGUMENT_EXCEPTION.getLabel();
        if (CollectionUtils.isNotEmpty(e.getConstraintViolations())) {
            message = CollectionUtils.join(e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()), StringConstants.SEMICOLON);
        }
        return R.error(ErrorCodeEnum.ILLEGAL_ARGUMENT_EXCEPTION.getValue(), message);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public <T> R<T> processException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        String message = ErrorCodeEnum.ILLEGAL_ARGUMENT_EXCEPTION.getLabel();
        if (CollectionUtils.isNotEmpty(e.getBindingResult().getAllErrors())) {
            message = CollectionUtils.join(e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()), StringConstants.SEMICOLON);
        }
        return R.error(ErrorCodeEnum.ILLEGAL_ARGUMENT_EXCEPTION.getValue(), message);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public <T> R<T> processException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException", e);
        return R.error(ErrorCodeEnum.ILLEGAL_ARGUMENT_EXCEPTION.getValue(), "类型错误");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public <T> R<T> processException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException", e);
        return R.error(ErrorCodeEnum.PARAM_IS_NULL);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ServletException.class)
    public <T> R<T> processException(ServletException e) {
        log.error("ServletException", e);
        return R.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NoHandlerFoundException.class)
    public <T> R<T> processException(NoHandlerFoundException e) {
        log.error("NoHandlerFoundException", e);
        return R.error(ErrorCodeEnum.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(IllegalArgumentException.class)
    public <T> R<T> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException", e);
        return R.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public <T> R<T> processException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException", e);
        String errorMessage = "请求体不可为空";
        Throwable cause = e.getCause();
        if (cause != null) {
            errorMessage = convertMessage(cause);
        }
        return R.error(errorMessage);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(TypeMismatchException.class)
    public <T> R<T> processException(TypeMismatchException e) {
        log.error("TypeMismatchException", e);
        return R.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public <T> R<T> processSqlSyntaxErrorException(SQLSyntaxErrorException e) {
        log.error("SQLSyntaxErrorException", e);
        return R.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(JSONException.class)
    public <T> R<T> handleException(JSONException e) {
        log.error("JSONException", e);
        return R.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Throwable.class)
    public <T> R<T> handleException(Throwable e) {
        log.error("Throwable", e);
        String message = convertMessage(e);
        return R.error(message);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public <T> R<T> handleException(Exception e) {
        log.error("Exception", e);
        return R.error(e.getLocalizedMessage());
    }

    /**
     * 传参类型错误时，用于消息转换
     *
     * @param throwable 异常
     * @return 错误信息
     */
    private String convertMessage(Throwable throwable) {
        String error = throwable.toString();
        String regulation = "\\[\"(.*?)\"]+";
        Pattern pattern = Pattern.compile(regulation);
        Matcher matcher = pattern.matcher(error);
        String group = "";
        if (matcher.find()) {
            String matchString = matcher.group();
            matchString = matchString.replace("[", "").replace("]", "");
            matchString = matchString.replaceAll("\"", "") + "字段类型错误";
            group += matchString;
        }
        return group;
    }

}
