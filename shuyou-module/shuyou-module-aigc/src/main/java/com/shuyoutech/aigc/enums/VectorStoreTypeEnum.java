package com.shuyoutech.aigc.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 向量数据库类型枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum VectorStoreTypeEnum implements BaseEnum<String, String> {

    /**
     * PGVector
     */
    PGVECTOR("1", "PGVector"),

    /**
     * Milvus
     */
    MILVUS("2", "Milvus"),

    /**
     * Elasticsearch
     */
    ELASTICSEARCH("3", "Elasticsearch");

    private final String value;
    private final String label;

}
