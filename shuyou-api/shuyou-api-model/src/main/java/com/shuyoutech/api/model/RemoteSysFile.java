package com.shuyoutech.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author YangChao
 * @date 2025-05-15 13:57
 **/
@Data
@Schema(description = "文件类")
public class RemoteSysFile implements Serializable {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "创建人ID")
    private String createUserId;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "原文件名称")
    private String originalFileName;

    @Schema(description = "文件大小")
    private Long fileSize;

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
