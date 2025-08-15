package com.shuyoutech.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YangChao
 * @date 2025-07-06 13:30
 **/
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum implements BaseEnum<Integer, String> {

    SUCCESS(0, "ok"),

    BAD_REQUEST(400, "请求参数不正确"),

    UNAUTHORIZED(401, "账号未登录"),

    ACCESS_DENIED(403, "您没有权限，拒绝访问"),

    NOT_FOUND(404, "请求未找到"),

    METHOD_NOT_ALLOWED(405, "请求方法不正确"),

    LOCKED(423, "请求失败，请稍后重试"),

    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后重试"),

    BAD_CREDENTIALS(450, "用户名或密码错误"),

    ACCOUNT_DISABLED(451, "该账户已经被禁用"),

    ACCOUNT_EXPIRED(452, "该账户已经过期"),

    ACCOUNT_LOCKED(453, "该账户已经被锁定"),

    ACCOUNT_ENDPOINT_LIMITED(454, "您已经使用其它终端登录,请先退出其它终端"),

    CREDENTIALS_EXPIRED(455, "该账户密码凭证已过期"),

    INTERNAL_SERVER_ERROR(500, "系统异常"),

    ILLEGAL_ARGUMENT_EXCEPTION(501, "参数不合法错误，请仔细确认参数使用是否正确。"),

    MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION(502, "接口参数使用错误或必要参数缺失，请查阅接口文档！"),

    NULL_POINTER_EXCEPTION(503, "后台代码执行过程中出现了空值"),

    TYPE_MISMATCH_EXCEPTION(504, "类型不匹配"),

    SERVICE_UNAVAILABLE(505, "服务不可用"),

    TEMPORARILY_UNAVAILABLE(506, "由于服务器临时超载或维护，授权服务器当前无法处理该请求"),

    REPEAT_SUBMIT(507, "重复提交"),

    NO_AUTH_SOURCE(508, "权限来源不能为空"),

    NOT_FOUND_AUTH_SOURCE(509, "权限来源不匹配"),

    NOT_CONFIG_AUTH_SOURCE(510, "权限来源没有配置"),

    PARAM_IS_NULL(800, "请求必填参数为空"),

    PARAM_ERROR(801, "请求参数错误"),

    UNKNOWN(999, "未知错误");

    private final Integer value;
    private final String label;

}
