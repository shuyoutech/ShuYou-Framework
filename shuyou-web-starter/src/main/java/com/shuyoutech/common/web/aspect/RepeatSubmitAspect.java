package com.shuyoutech.common.web.aspect;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.shuyoutech.common.core.constant.CommonConstants;
import com.shuyoutech.common.core.constant.StringConstants;
import com.shuyoutech.common.core.enums.ErrorCodeEnum;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.util.BooleanUtils;
import com.shuyoutech.common.core.util.SmUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.redis.constant.CacheConstants;
import com.shuyoutech.common.redis.util.RedisUtils;
import com.shuyoutech.common.web.annotation.RepeatSubmit;
import com.shuyoutech.common.web.util.JakartaServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.StringJoiner;

/**
 * @author YangChao
 * @since 2025-04-06 16:45
 **/
@Slf4j
@Aspect
@Component
public class RepeatSubmitAspect {

    private static final ThreadLocal<String> KEY_CACHE = new ThreadLocal<>();

    @Around("@annotation(repeatSubmit)")
    public Object around(ProceedingJoinPoint point, RepeatSubmit repeatSubmit) {
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
        String submitKey = SmUtils.sm3(argParams);
        // 唯一标识（指定key + userId + 消息体）
        String cacheRepeatKey = StringUtils.isBlank(userId) ? CacheConstants.REPEAT_SUBMIT_KEY + submitKey : CacheConstants.REPEAT_SUBMIT_KEY + userId + ":" + submitKey;
        Boolean success = RedisUtils.setIfAbsent(cacheRepeatKey, "1", Duration.ofMillis(interval));
        if (BooleanUtils.isFalse(success)) {
            throw new BusinessException(ErrorCodeEnum.REPEAT_SUBMIT.getValue(), "不允许重复提交，请稍后再试");
        }
        Object proceed = null;
        try {
            proceed = point.proceed();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtils.delete(cacheRepeatKey);
        }
        return proceed;
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