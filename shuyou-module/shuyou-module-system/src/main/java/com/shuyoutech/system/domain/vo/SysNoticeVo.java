package com.shuyoutech.system.domain.vo;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.mongodb.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author YangChao
 * @date 2025-07-07 10:55:00
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "通知公告显示类")
public class SysNoticeVo extends BaseVo {

    /**
     * 枚举 {@link StatusEnum}
     */
    @Schema(description = "状态:0-停用,1-正常")
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
