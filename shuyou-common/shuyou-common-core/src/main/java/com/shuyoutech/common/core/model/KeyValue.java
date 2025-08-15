package com.shuyoutech.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-14 10:13
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValue<K, V> implements Serializable {

    @Schema(description = "键")
    private K key;

    @Schema(description = "值")
    private V value;

}
