package com.shuyoutech.common.core.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 13:03
 **/
@Data
public abstract class AbstractRunnable<E> implements Runnable {

    @Schema(description = "数据集合")
    private List<E> list;

    @Schema(description = "起始位置")
    private int start;

    @Schema(description = "终止位置")
    private int end;

    @Schema(description = "总笔数")
    private long total;
}
