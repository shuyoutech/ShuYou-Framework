package com.shuyoutech.common.core.enums;

/**
 * @author YangChao
 * @date 2025-07-05 20:10
 **/
public interface BaseEnum<K,V> {

    /**
     * 获取枚举值
     *
     * @return 枚举值
     */
    K getValue();

    /**
     * 获取枚举名称
     *
     * @return 枚举名称
     */
    V getLabel();

}
