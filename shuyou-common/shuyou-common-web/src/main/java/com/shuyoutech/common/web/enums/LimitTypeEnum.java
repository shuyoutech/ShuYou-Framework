package com.shuyoutech.common.web.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YangChao
 * @date 2025-04-08 16:53
 **/
@Getter
@AllArgsConstructor
public enum LimitTypeEnum implements BaseEnum<Integer, String> {

    DEFAULT(1, "默认策略全局限流"),

    IP(2, "根据请求者IP进行限流"),

    CLUSTER(3, "实例限流");

    private final Integer value;
    private final String label;

}
