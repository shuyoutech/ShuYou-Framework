package com.shuyoutech.common.mongodb.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-14 13:45
 **/
@Data
public class BaseVo implements Serializable {

    @Schema(description = "主键")
    private String id;

}
