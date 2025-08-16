package com.shuyoutech.system.domain.bo;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.core.model.StatusGroup;
import com.shuyoutech.common.core.model.UpdateGroup;
import com.shuyoutech.system.domain.entity.SysNoticeEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-07 10:55:00
 **/
@Data
@AutoMapper(target = SysNoticeEntity.class)
@Schema(description = "通知公告类")
public class SysNoticeBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

    /**
     * 枚举 {@link StatusEnum}
     */
    @NotNull(message = "状态不能为空", groups = {StatusGroup.class})
    @Schema(description = "状态")
    private String status;

    @Schema(description = "公告类型:1-通知,2-公告")
    private Integer noticeType;

    @Schema(description = "公告标题")
    private String noticeTitle;

    @Schema(description = "公告内容")
    private String noticeContent;

    @Schema(description = "公告描述")
    private String noticeDesc;

}
