package com.shuyoutech.common.core.service;

import java.util.Collection;

/**
 * @author YangChao
 * @date 2025-07-06 14:48
 **/
@FunctionalInterface
public interface BatchHandler {

    /**
     * 分批处理数据
     *
     * @param list 数据集合
     */
    <T> void handle(Collection<T> list);

}
