package com.shuyoutech.common.disruptor.event;

import com.lmax.disruptor.EventFactory;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.disruptor.model.DisruptorData;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-04-06 18:25
 **/
@Data
public class DisruptorEvent implements Serializable {

    public static final EventFactory<DisruptorEvent> FACTORY = DisruptorEvent::new;

    private String key;

    private DisruptorData value;

    @Override
    public String toString() {
        return StringUtils.format("DisruptorEvent{ key = {}, value = {} }", key, value);
    }

}
