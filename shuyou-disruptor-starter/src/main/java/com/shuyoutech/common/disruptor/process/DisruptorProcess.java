package com.shuyoutech.common.disruptor.process;

import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.shuyoutech.common.disruptor.event.DisruptorEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author YangChao
 * @date 2025-04-06 18:29
 **/
@Slf4j
public class DisruptorProcess {

    /**
     * ringBUFFER_SIZE为RingBuffer缓冲区大小，最好是2的指数倍 256kB
     */
    public static final int DEFAULT_BUFFER_SIZE = 256 * 1024;

    /**
     * 线程池，进行Disruptor内部的数据接收处理调用
     */
    public static final ThreadFactory DEFAULT_THREAD_FACTORY = Executors.defaultThreadFactory();

    /**
     * ProducerType.SINGLE和ProducerType.MULTI，用来指定数据生成者有一个还是多个
     */
    public static final ProducerType DEFAULT_PRODUCER_TYPE = ProducerType.SINGLE;

    /**
     * 一种策略，用来均衡数据生产者和消费者之间的处理效率(BlockingWaitStrategy 、SleepingWaitStrategy 、YieldingWaitStrategy、BusySpinWaitStrategy)
     */
    public static final WaitStrategy DEFAULT_WAIT_STRATEGY = new YieldingWaitStrategy();

    /**
     * 获取Disruptor
     */
    public static Disruptor<DisruptorEvent> getDefaultDisruptor() {
        return new Disruptor<>(DisruptorEvent.FACTORY, DEFAULT_BUFFER_SIZE, DEFAULT_THREAD_FACTORY, DEFAULT_PRODUCER_TYPE, DEFAULT_WAIT_STRATEGY);
    }

    /**
     * 创建一个自定义的Disruptor
     *
     * @param bufferSize    RingBuffer缓冲区大小
     * @param threadFactory 线程池
     * @param producerType  用来指定数据生成者有一个还是多个
     * @param waitStrategy  策略用来均衡数据生产者和消费者之间的处理效率
     */
    public static Disruptor<DisruptorEvent> getDisruptor(int bufferSize, ThreadFactory threadFactory, ProducerType producerType, WaitStrategy waitStrategy) {
        return new Disruptor<>(DisruptorEvent.FACTORY, bufferSize, threadFactory, producerType, waitStrategy);
    }

}
