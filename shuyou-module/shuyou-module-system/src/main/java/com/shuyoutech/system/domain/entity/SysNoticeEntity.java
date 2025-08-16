package com.shuyoutech.system.domain.entity;

import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.mongodb.model.BaseEntity;
import com.shuyoutech.system.domain.vo.SysNoticeVo;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author YangChao
 * @date 2025-07-07 00:04
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysNoticeVo.class)
@Schema(description = "通知公告表")
@Document(collection = "sys_notice")
public class SysNoticeEntity extends BaseEntity<SysNoticeEntity> {

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
