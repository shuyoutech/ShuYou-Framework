package com.shuyoutech.common.mongodb.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-08-14 13:45
 **/
@Data
public class BaseEntity<T extends BaseEntity<T>> implements Serializable {

    @Id
    @Field(value = "_id")
    @Schema(description = "主键")
    private String id;

}
