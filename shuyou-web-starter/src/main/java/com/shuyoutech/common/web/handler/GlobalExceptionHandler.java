package com.shuyoutech.common.web.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
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
 * @since 2025-04-06 16:27
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NotPermissionException.class)
    public R<Void> handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("handleNotPermissionException request url:{},error:{}", requestURI, e.getMessage());
        return R.error(ErrorCodeEnum.ACCESS_DENIED.getValue(), "没有访问权限，请联系管理员授权");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NotRoleException.class)
    public R<Void> handleNotRoleException(NotRoleException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("handleNotRoleException request url:{},error:{}", requestURI, e.getMessage());
        return R.error(ErrorCodeEnum.ACCESS_DENIED.getValue(), "没有访问权限，请联系管理员授权");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NotLoginException.class)
    public R<Void> handleNotLoginException(NotLoginException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("handleNotLoginException request url:{},error:{}", requestURI, e.getMessage());
        return R.error(ErrorCodeEnum.UNAUTHORIZED.getValue(), "认证失败，无法访问系统资源");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(SaTokenException.class)
    public R<Void> handleSaTokenException(SaTokenException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("handleSaTokenException request url:{},error:{}", requestURI, e.getMessage());
        return R.error(ErrorCodeEnum.UNAUTHORIZED.getValue(), "认证失败，无法访问系统资源");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    public <T> R<T> handleException(BusinessException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("handleException request url:{},error:{}", requestURI, e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BindException.class)
    public <T> R<T> processException(BindException e) {
        log.error("BindException error:{}", e.getMessage());
        String message = ErrorCodeEnum.PARAM_ERROR.getLabel();
        if (CollectionUtils.isNotEmpty(e.getAllErrors())) {
            message = CollectionUtils.join(e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()), StringConstants.SEMICOLON);
        }
        return R.error(ErrorCodeEnum.PARAM_ERROR.getValue(), message);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ConstraintViolationException.class)
    public <T> R<T> processException(ConstraintViolationException e) {
        log.error("ConstraintViolationException error:{}", e.getMessage());
        String message = ErrorCodeEnum.PARAM_ERROR.getLabel();
        if (CollectionUtils.isNotEmpty(e.getConstraintViolations())) {
            message = CollectionUtils.join(e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()), StringConstants.SEMICOLON);
        }
        return R.error(ErrorCodeEnum.PARAM_ERROR.getValue(), message);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public <T> R<T> processException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException error:{}", e.getMessage());
        String message = ErrorCodeEnum.PARAM_ERROR.getLabel();
        if (CollectionUtils.isNotEmpty(e.getBindingResult().getAllErrors())) {
            message = CollectionUtils.join(e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()), StringConstants.SEMICOLON);
        }
        return R.error(ErrorCodeEnum.PARAM_ERROR.getValue(), message);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public <T> R<T> processException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException error:{}", e.getMessage());
        return R.error(ErrorCodeEnum.PARAM_ERROR.getValue(), "类型错误");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public <T> R<T> processException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException error:{}", e.getMessage());
        return R.error(ErrorCodeEnum.PARAM_IS_NULL);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ServletException.class)
    public <T> R<T> processException(ServletException e) {
        log.error("ServletException error:{}", e.getMessage());
        return R.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NoHandlerFoundException.class)
    public <T> R<T> processException(NoHandlerFoundException e) {
        log.error("NoHandlerFoundException error:{}", e.getMessage());
        return R.error(ErrorCodeEnum.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(IllegalArgumentException.class)
    public <T> R<T> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException error:{}", e.getMessage());
        return R.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public <T> R<T> processException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException error:{}", e.getMessage());
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
        log.error("TypeMismatchException error:{}", e.getMessage());
        return R.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public <T> R<T> processSqlSyntaxErrorException(SQLSyntaxErrorException e) {
        log.error("SQLSyntaxErrorException error:{}", e.getMessage());
        return R.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(JSONException.class)
    public <T> R<T> handleException(JSONException e) {
        log.error("JSONException error:{}", e.getMessage());
        return R.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Throwable.class)
    public <T> R<T> handleException(Throwable e) {
        log.error("Throwable error:{}", e.getMessage());
        String message = convertMessage(e);
        return R.error(message);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public <T> R<T> handleException(Exception e) {
        log.error("Exception error:{}", e.getMessage());
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
