package com.shuyoutech.common.web.config;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.ContextValueFilter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import com.shuyoutech.common.core.constant.DateConstants;
import com.shuyoutech.common.core.util.NumberUtils;
import com.shuyoutech.common.web.interceptor.TraceIdInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-04-06 15:36
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TraceIdInterceptor()).addPathPatterns("/**").order(-200);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许任何来源
                .allowedOriginPatterns("*")
                // 允许任何请求头
                .allowedHeaders(CorsConfiguration.ALL)
                // 允许任何方法
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "PATCH", "DELETE")
                // 允许凭证
                .allowCredentials(true).maxAge(3600);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**") //
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建fastJson消息转换器
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        // 创建配置类
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setReaderFeatures(
                // 初始化String字段为空字符串""
                JSONReader.Feature.InitStringFieldAsEmpty);
        fastJsonConfig.setWriterFeatures(
                // 基于字段反序列化，如果不配置，会默认基于public的field和getter方法序列化。配置后，会基于非static的field（包括private）做反序列化。
                JSONWriter.Feature.FieldBased,
                // 保留map空的字段
                JSONWriter.Feature.WriteMapNullValue,
                // 将List类型的null转成[]
                JSONWriter.Feature.WriteNullListAsEmpty,
                // 将String类型的null转成""
                JSONWriter.Feature.WriteNullStringAsEmpty,
                // 将Boolean类型的null转成false
                JSONWriter.Feature.WriteNullBooleanAsFalse,
                // 日期格式转换
                JSONWriter.Feature.PrettyFormat,
                // 将空置输出为缺省值，Number类型的null都输出为0，String类型的null输出为""，数组和Collection类型的输出为[]
                JSONWriter.Feature.NullAsDefaultValue);
        fastJsonConfig.setWriterFilters((ContextValueFilter) (context, object, name, value) -> {
            if (value instanceof Number) {
                if (value instanceof Long || value instanceof BigInteger) {
                    return NumberUtils.toStr((Number) value);
                } else if (value instanceof Integer) {
                    return new BigDecimal((Integer) value);
                }
                String valueStr = NumberUtils.toStr((Number) value);
                return new BigDecimal(valueStr);
            }
            return value;
        });
        fastJsonConfig.setDateFormat(DateConstants.DATETIME_FORMAT);
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        converters.addFirst(fastConverter);
    }

}
