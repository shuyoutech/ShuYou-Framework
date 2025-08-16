package com.shuyoutech.system.domain.entity;

import com.shuyoutech.common.mongodb.model.BaseEntity;
import com.shuyoutech.system.domain.vo.SysDictDataVo;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author YangChao
 * @date 2025-07-07 19:51
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysDictDataVo.class)
@Schema(description = "字典数据表")
@Document(collection = "sys_dict_data")
public class SysDictDataEntity extends BaseEntity<SysDictDataEntity> {

    @Schema(description = "字典类型ID")
    private String dictTypeId;

    @Schema(description = "字典标签")
    private String dictLabel;

    @Schema(description = "字典值")
    private String dictValue;

    @Schema(description = "字典排序")
    private Integer dictSort;

}
