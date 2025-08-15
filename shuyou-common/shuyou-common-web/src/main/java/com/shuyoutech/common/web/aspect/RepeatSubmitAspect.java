package com.shuyoutech.common.web.aspect;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson2.JSON;
import com.shuyoutech.common.core.constant.CommonConstants;
import com.shuyoutech.common.core.constant.StringConstants;
import com.shuyoutech.common.core.enums.ErrorCodeEnum;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.model.R;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.redis.constant.CacheConstants;
import com.shuyoutech.common.redis.util.RedisUtils;
import com.shuyoutech.common.web.annotation.RepeatSubmit;
import com.shuyoutech.common.web.util.JakartaServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.StringJoiner;

/**
 * @author YangChao
 * @date 2025-04-06 16:45
 **/
@Slf4j
@Aspect
@Component
public class RepeatSubmitAspect {

    private static final ThreadLocal<String> KEY_CACHE = new ThreadLocal<>();

    @Before("@annotation(repeatSubmit)")
    public void doBefore(JoinPoint point, RepeatSubmit repeatSubmit) {
        // 如果注解不为0 则使用注解数值
        long interval = repeatSubmit.timeUnit().toMillis(repeatSubmit.interval());
        if (interval < 1000) {
            throw new BusinessException(ErrorCodeEnum.REPEAT_SUBMIT.getValue(), "重复提交间隔时间不能小于1秒");
        }
        // 请求参数
        String argParams = argsArrayToString(point.getArgs());
        // 当前用户ID
        String userId = JakartaServletUtils.getHeader(JakartaServletUtils.getRequest(), CommonConstants.USER_ID, CommonConstants.CHARSET_UTF_8);
        // 唯一值
        String submitKey = SecureUtil.md5(argParams);
        // 唯一标识（指定key + userId + 消息体）
        String cacheRepeatKey = StringUtils.isBlank(userId) ? CacheConstants.REPEAT_SUBMIT_KEY + submitKey : CacheConstants.REPEAT_SUBMIT_KEY + userId + ":" + submitKey;
        if (RedisUtils.setIfAbsent(cacheRepeatKey, StringConstants.EMPTY, Duration.ofMillis(interval))) {
            KEY_CACHE.set(cacheRepeatKey);
        } else {
            throw new BusinessException(ErrorCodeEnum.REPEAT_SUBMIT.getValue(), "不允许重复提交，请稍后再试");
        }
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(repeatSubmit)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, RepeatSubmit repeatSubmit, Object jsonResult) {
        if (jsonResult instanceof R<?> r) {
            try {
                // 成功则不删除redis数据 保证在有效时间内无法重复提交
                if (r.getCode() == R.SUCCESS) {
                    return;
                }
                RedisUtils.delete(KEY_CACHE.get());
            } finally {
                KEY_CACHE.remove();
            }
        }
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(repeatSubmit)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, RepeatSubmit repeatSubmit, Exception e) {
        RedisUtils.delete(KEY_CACHE.get());
        KEY_CACHE.remove();
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringJoiner params = new StringJoiner(StringConstants.SPACE);
        if (ArrayUtil.isEmpty(paramsArray)) {
            return params.toString();
        }
        for (Object o : paramsArray) {
            if (ObjectUtil.isNotNull(o)) {
                params.add(JSON.toJSONString(o));
            }
        }
        return params.toString();
    }

}