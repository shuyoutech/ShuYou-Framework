package com.shuyoutech.common.disruptor.handler;

import com.lmax.disruptor.RingBuffer;
import com.shuyoutech.common.disruptor.event.DisruptorEvent;
import com.shuyoutech.common.disruptor.model.DisruptorData;

/**
 * @author YangChao
 * @date 2025-03-26 21:39
 **/
public class DisruptorProducer {

    /**
     * 生产者持有RingBuffer实例，可以直接向RingBuffer实例中的entry写入数据
     */
    private final RingBuffer<DisruptorEvent> ringBuffer;

    public DisruptorProducer(RingBuffer<DisruptorEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void pushData(DisruptorData value) {
        long sequence = ringBuffer.next();
        try {
            DisruptorEvent event = ringBuffer.get(sequence);
            event.setKey(String.valueOf(sequence));
            event.setValue(value);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

}
