package com.shuyoutech.common.disruptor.handler;

import com.lmax.disruptor.EventHandler;
import com.shuyoutech.common.disruptor.event.DisruptorEvent;

/**
 * disruptor 消息处理机制,Disruptor 定义的事件处理接口，由用户实现，用于处理事件，是 Consumer 的真正实现
 *
 * @author YangChao
 * @date 2025-04-06 18:24
 **/
public abstract class AbstractEventHandler implements EventHandler<DisruptorEvent> {

    /**
     * event为从RingBuffer entry中读取的事件内容，消费者从event中读取数据，并完成业务逻辑处理
     *
     * @param event      数据事件
     * @param sequence   序号
     * @param endOfBatch 是否批量
     */
    @Override
    public abstract void onEvent(DisruptorEvent event, long sequence, boolean endOfBatch) throws Exception;

}
