package com.shuyoutech.common.disruptor.service;

import com.shuyoutech.common.disruptor.model.DisruptorData;

/**
 * @author YangChao
 * @date 2025-07-19 12:42
 **/
public interface DisruptorService {

    String serviceName();

    void consume(DisruptorData data);

}
