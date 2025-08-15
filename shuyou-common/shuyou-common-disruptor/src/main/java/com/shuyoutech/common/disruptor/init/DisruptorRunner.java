package com.shuyoutech.common.disruptor.init;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.shuyoutech.common.disruptor.event.DisruptorEvent;
import com.shuyoutech.common.disruptor.handler.ConsumerEventHandler;
import com.shuyoutech.common.disruptor.handler.DisruptorProducer;
import com.shuyoutech.common.disruptor.service.DisruptorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * @author YangChao
 * @date 2025-07-19 12:09
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class DisruptorRunner implements CommandLineRunner, DisposableBean, ApplicationContextAware {

    public static DisruptorProducer disruptorProducer;
    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) {
        Map<String, DisruptorService> beanMap = applicationContext.getBeansOfType(DisruptorService.class);
        for (DisruptorService disruptorService : beanMap.values()) {
            DISRUPTOR_SERVICE_MAP.put(disruptorService.serviceName(), disruptorService);
        }
        // 缓冲区大小，必需是2的N次方
        int ringBufferSize = 256 * 1024;
        // ProducerType.SINGLE（表示生产者只有一个）和ProducerType.MULTY（表示有多个生产者）
        ProducerType producerType = ProducerType.SINGLE;
        //  YieldingWaitStrategy 的性能是最好的，适合用于低延迟的系统。在要求极高性能且事件处理线数小于CPU逻辑核心数的场景中，推荐使用此策略；例如，CPU开启超线程的特性
        YieldingWaitStrategy waitStrategy = new YieldingWaitStrategy();
        Disruptor<DisruptorEvent> disruptor = new Disruptor<>(DisruptorEvent.FACTORY, ringBufferSize, Executors.defaultThreadFactory(), producerType, waitStrategy);
        disruptor.handleEventsWith(new ConsumerEventHandler());
        RingBuffer<DisruptorEvent> ringBuffer = disruptor.start();
        disruptorProducer = new DisruptorProducer(ringBuffer);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
