package com.shuyoutech.common.web.util;

import com.shuyoutech.common.disruptor.model.DisruptorData;

import static com.shuyoutech.common.disruptor.init.DisruptorRunner.disruptorProducer;

/**
 * @author YangChao
 * @since 2025-10-21 15:37
 **/
public class DisruptorUtils {

    /**
     * 推送数据
     *
     * @param serviceName 服务名称
     * @param data        数据
     */
    public static void pushData(String serviceName, Object data) {
        DisruptorData disruptorData = new DisruptorData();
        disruptorData.setServiceName(serviceName);
        disruptorData.setData(data);
        disruptorProducer.pushData(disruptorData);
    }

}
