package com.shuyoutech.common.web.aspect;

import cn.hutool.core.util.ArrayUtil;
import com.shuyoutech.common.core.constant.StringConstants;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.redis.constant.CacheConstants;
import com.shuyoutech.common.redis.util.RedissonUtils;
import com.shuyoutech.common.web.annotation.RateLimiter;
import com.shuyoutech.common.web.enums.LimitTypeEnum;
import com.shuyoutech.common.web.util.JakartaServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RateType;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * @author YangChao
 * @date 2025-04-08 16:56
 **/
@Slf4j
@Aspect
public class RateLimiterAspect {

    /**
     * 定义spel表达式解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();
    /**
     * 定义spel解析模版
     */
    private final ParserContext parserContext = new TemplateParserContext();
    /**
     * 定义spel上下文对象进行解析
     */
    private final EvaluationContext context = new StandardEvaluationContext();
    /**
     * 方法参数解析器
     */
    private final ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();

    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) {
        int time = rateLimiter.time();
        int count = rateLimiter.count();
        String combineKey = getCombineKey(rateLimiter, point);
        try {
            RateType rateType = RateType.OVERALL;
            if (LimitTypeEnum.CLUSTER == rateLimiter.limitType()) {
                rateType = RateType.PER_CLIENT;
            }
            long number = RedissonUtils.rateLimiter(combineKey, rateType, count, time);
            if (number == -1) {
                String message = rateLimiter.message();
                throw new BusinessException(message);
            }
            log.info("限制令牌 => {}, 剩余令牌 => {}, 缓存key => '{}'", count, number, combineKey);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            } else {
                throw new RuntimeException("服务器限流异常，请稍候再试");
            }
        }
    }

    public String getCombineKey(RateLimiter rateLimiter, JoinPoint point) {
        String key = rateLimiter.key();
        // 获取方法(通过方法签名来获取)
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        // 判断是否是spel格式
        if (StringUtils.containsAny(key, StringConstants.HASHTAG)) {
            // 获取参数值
            Object[] args = point.getArgs();
            // 获取方法上参数的名称
            String[] parameterNames = pnd.getParameterNames(method);
            if (ArrayUtil.isEmpty(parameterNames)) {
                throw new BusinessException("限流key解析异常!请联系管理员!");
            }
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
            // 解析返回给key
            try {
                Expression expression;
                if (StringUtils.startsWith(key, parserContext.getExpressionPrefix()) && StringUtils.endsWith(key, parserContext.getExpressionSuffix())) {
                    expression = parser.parseExpression(key, parserContext);
                } else {
                    expression = parser.parseExpression(key);
                }
                key = expression.getValue(context, String.class) + ":";
            } catch (Exception e) {
                throw new BusinessException("限流key解析异常!请联系管理员!");
            }
        }
        StringBuilder stringBuffer = new StringBuilder(CacheConstants.RATE_LIMIT_KEY);
        stringBuffer.append(JakartaServletUtils.getRequest().getRequestURI()).append(":");
        if (LimitTypeEnum.IP  == rateLimiter.limitType() ) {
            // 获取请求ip
            stringBuffer.append(JakartaServletUtils.getClientIP(JakartaServletUtils.getRequest())).append(":");
        } else if (LimitTypeEnum.CLUSTER  == rateLimiter.limitType()) {
            // 获取客户端实例id
            stringBuffer.append(RedissonUtils.getClient().getId()).append(":");
        }
        return stringBuffer.append(key).toString();
    }

}
