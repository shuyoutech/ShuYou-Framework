package com.shuyoutech.common.disruptor.handler;

import cn.hutool.core.map.MapUtil;
import com.lmax.disruptor.EventHandler;
import com.shuyoutech.common.disruptor.event.DisruptorEvent;
import com.shuyoutech.common.disruptor.model.DisruptorData;
import com.shuyoutech.common.disruptor.service.DisruptorService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author YangChao
 * @date 2025-07-19 12:11
 **/
@Slf4j
public class ConsumerEventHandler implements EventHandler<DisruptorEvent> {

    public static final Map<String, DisruptorService> DISRUPTOR_SERVICE_MAP = MapUtil.newConcurrentHashMap();

    @Override
    public void onEvent(DisruptorEvent event, long sequence, boolean endOfBatch) {
        try {
            DisruptorData data = event.getValue();
            DisruptorService service = DISRUPTOR_SERVICE_MAP.get(data.getServiceName());
            if (null == service) {
                log.error("onEvent ============ serviceName:{} is not match target", data.getServiceName());
                return;
            }
            service.consume(data);
            // log.info("onEvent ============ event:{},sequence:{},endOfBatch:{}", JSON.toJSONString(event), sequence, endOfBatch);
        } catch (Exception e) {
            log.error("onEvent ============ exception : {}", e.getMessage());
        }
    }
}
