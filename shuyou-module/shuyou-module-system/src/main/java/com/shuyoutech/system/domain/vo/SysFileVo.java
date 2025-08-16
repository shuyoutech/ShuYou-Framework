package com.shuyoutech.system.domain.vo;

import com.shuyoutech.api.model.RemoteSysFile;
import com.shuyoutech.common.mongodb.model.BaseVo;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author YangChao
 * @date 2025-07-07 20:06
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@AutoMappers({@AutoMapper(target = RemoteSysFile.class)})
@Schema(description = "文件Result类")
public class SysFileVo extends BaseVo {

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "创建人ID")
    private String createUserId;

    @Schema(description = "创建人名称")
    private String createUserName;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "原文件名称")
    private String originalFileName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "文件大小格式")
    private String fileSizeFormat;

    @Schema(description = "文件格式")
    private String fileType;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件Hash")
    private String fileHash;

    @Schema(description = "桶名称")
    private String bucketName;

    @Schema(description = "oss文件key")
    private String ossFileKey;

    @Schema(description = "预览url")
    private String previewUrl;

}
