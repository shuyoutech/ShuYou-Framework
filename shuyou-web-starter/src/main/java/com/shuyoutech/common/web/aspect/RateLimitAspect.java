package com.shuyoutech.common.web.aspect;

import com.shuyoutech.common.core.constant.StringConstants;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.redis.constant.CacheConstants;
import com.shuyoutech.common.redis.util.RedissonUtils;
import com.shuyoutech.common.web.annotation.RateLimit;
import com.shuyoutech.common.web.enums.LimitTypeEnum;
import com.shuyoutech.common.web.util.JakartaServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
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
import java.time.Duration;

/**
 * @author YangChao
 * @since 2025-04-08 16:56
 **/
@Slf4j
@Aspect
public class RateLimitAspect {

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

    @Before("@annotation(rateLimit)")
    public void doBefore(JoinPoint point, RateLimit rateLimit) {
        String combineKey = getCombineKey(rateLimit, point);
        RateType rateType;
        if (LimitTypeEnum.CLUSTER == rateLimit.limitType()) {
            rateType = RateType.PER_CLIENT;
        } else {
            rateType = RateType.OVERALL;
        }
        RedissonClient redissonClient = RedissonUtils.getClient();
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(combineKey);
        // 设置限流速率：每 time 秒产生 count 个令牌
        rateLimiter.trySetRate(rateType, rateLimit.count(), Duration.ofSeconds(rateLimit.time()));
        // 可选：设置key的过期时间，避免不常用的限流key长期占用内存 过期时间设为时间窗口的两倍
        long ttl = rateLimit.time() * 2L;
        rateLimiter.expire(Duration.ofSeconds(ttl));
        if (rateLimiter.tryAcquire()) {
            log.info("限制令牌 => {}, 剩余令牌 => {}, 缓存key => '{}'", rateLimit.count(), rateLimiter.availablePermits(), combineKey);
        } else {
            log.error("触发限流, 缓存key => '{}'", combineKey);
            throw new BusinessException(rateLimit.message());
        }
    }

    public String getCombineKey(RateLimit rateLimit, JoinPoint point) {
        String key = rateLimit.key();
        // 获取方法(通过方法签名来获取)
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        // 判断是否是spel格式
        if (StringUtils.containsAny(key, StringConstants.HASHTAG)) {
            // 获取参数值
            Object[] args = point.getArgs();
            // 获取方法上参数的名称
            String[] parameterNames = pnd.getParameterNames(method);
            if (null != parameterNames) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }
            // 解析返回给key
            try {
                Expression expression;
                if (StringUtils.startWith(key, parserContext.getExpressionPrefix()) && StringUtils.endWith(key, parserContext.getExpressionSuffix())) {
                    expression = parser.parseExpression(key, parserContext);
                } else {
                    expression = parser.parseExpression(key);
                }
                key = expression.getValue(context, String.class) + ":";
            } catch (Exception e) {
                throw new BusinessException("限流key解析异常!请联系管理员");
            }
        }
        StringBuilder stringBuffer = new StringBuilder(CacheConstants.RATE_LIMIT_KEY);
        stringBuffer.append(key).append(":");
        if (LimitTypeEnum.IP == rateLimit.limitType()) {
            // 获取请求ip
            stringBuffer.append(JakartaServletUtils.getClientIP(JakartaServletUtils.getRequest()));
        } else if (LimitTypeEnum.CLUSTER == rateLimit.limitType()) {
            // 获取客户端实例id
            stringBuffer.append(RedissonUtils.getClient().getId());
        }
        return stringBuffer.toString();
    }

}
