package com.shuyoutech.common.disruptor.init;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.shuyoutech.common.core.util.MapUtils;
import com.shuyoutech.common.disruptor.event.DisruptorEvent;
import com.shuyoutech.common.disruptor.handler.ConsumerEventHandler;
import com.shuyoutech.common.disruptor.handler.DisruptorProducer;
import com.shuyoutech.common.disruptor.service.DisruptorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Executors;

import static com.shuyoutech.common.disruptor.handler.ConsumerEventHandler.DISRUPTOR_SERVICE_MAP;

/**
 * Disruptor 初始化运行器
 * <p>
 * 负责在Spring Boot应用启动时初始化Disruptor高性能队列框架
 * <p>
 * 功能：
 * <ul>
 *     <li>自动扫描并注册所有 DisruptorService实现类</li>
 *     <li>初始化Disruptor RingBuffer（缓冲区大小256KB）</li>
 *     <li>配置等待策略（YieldingWaitStrategy，适合低延迟场景）</li>
 *     <li>创建并启动 Disruptor实例</li>
 *     <li>应用关闭时优雅关闭 Disruptor</li>
 * </ul>
 * <p>
 * 配置说明：
 * <ul>
 *     <li>缓冲区大小：256 * 1024（必须是2的N次方）</li>
 *     <li>生产者类型：SINGLE（单生产者模式）</li>
 *     <li>等待策略：YieldingWaitStrategy（适合低延迟系统）</li>
 * </ul>
 *
 * @author YangChao
 * @since 2025-07-19 12:09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DisruptorRunner implements CommandLineRunner, DisposableBean, ApplicationContextAware {

    /**
     * Spring 应用上下文
     */
    private ApplicationContext applicationContext;

    /**
     * Disruptor 实例
     */
    private Disruptor<DisruptorEvent> disruptor;

    /**
     * Disruptor 生产者实例
     * <p>
     * 使用 volatile保证多线程可见性
     */
    public static volatile DisruptorProducer disruptorProducer;

    /**
     * 应用启动时执行
     * <p>
     * 执行流程：
     * <ol>
     *     <li>扫描并注册所有 DisruptorService实现类到服务映射表</li>
     *     <li>配置 Disruptor参数（缓冲区大小、生产者类型、等待策略）</li>
     *     <li>创建 Disruptor实例并注册事件处理器</li>
     *     <li>启动 Disruptor并创建生产者实例</li>
     * </ol>
     *
     * @param args 命令行参数
     */
    @Override
    public void run(String... args) {
        // 获取所有 DisruptorService 实现类并注册到服务映射表
        Map<String, DisruptorService> beanMap = applicationContext.getBeansOfType(DisruptorService.class);
        if (MapUtils.isNotEmpty(beanMap)) {
            for (DisruptorService disruptorService : beanMap.values()) {
                DISRUPTOR_SERVICE_MAP.put(disruptorService.serviceName(), disruptorService);
            }
        }

        // 配置Disruptor参数
        // 缓冲区大小：256 * 1024，必须是2的N次方
        int ringBufferSize = 256 * 1024;
        // 生产者类型：SINGLE（单生产者）或MULTI（多生产者）
        ProducerType producerType = ProducerType.SINGLE;
        // 等待策略：YieldingWaitStrategy性能最好，适合低延迟系统
        // 适用于事件处理线程数小于CPU逻辑核心数的场景，如CPU开启超线程时
        YieldingWaitStrategy waitStrategy = new YieldingWaitStrategy();

        // 创建 Disruptor实例
        disruptor = new Disruptor<>(DisruptorEvent.FACTORY, ringBufferSize, Executors.defaultThreadFactory(), producerType, waitStrategy);

        // 注册事件处理器
        disruptor.handleEventsWith(new ConsumerEventHandler());

        // 启动 Disruptor并获取RingBuffer
        RingBuffer<DisruptorEvent> ringBuffer = disruptor.start();

        // 创建生产者实例
        disruptorProducer = new DisruptorProducer(ringBuffer);
    }

    /**
     * 应用关闭时执行
     * <p>
     * 优雅关闭Disruptor，等待所有事件处理完成
     */
    @Override
    public void destroy() {
        if (disruptor != null) {
            disruptor.shutdown();
        }
    }

    /**
     * 设置 Spring 应用上下文
     *
     * @param applicationContext Spring 应用上下文
     * @throws BeansException Bean 异常
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
