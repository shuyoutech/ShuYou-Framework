package com.shuyoutech.system.domain.vo;

import com.shuyoutech.common.mongodb.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author YangChao
 * @date 2025-05-07 14:14
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "日志详情类")
public class SysLogVo extends BaseVo {

    @Schema(description = "链路追踪ID")
    private String traceId;

    @Schema(description = "操作模块")
    private String title;

    @Schema(description = "操作类型:1-新增,2-修改,3-删除,4-查看,5-授权")
    private Integer operType;

    @Schema(description = "请求类")
    private String className;

    @Schema(description = "请求方法")
    private String methodName;

    @Schema(description = "请求方式")
    private String reqMethod;

    @Schema(description = "请求用户ID")
    private String userId;

    @Schema(description = "请求用户名称")
    private String userName;

    @Schema(description = "请求用户机构ID")
    private String orgId;

    @Schema(description = "请求用户机构名称")
    private String orgName;

    @Schema(description = "请求url")
    private String reqUrl;

    @Schema(description = "请求IP")
    private String reqIp;

    @Schema(description = "请求ip region")
    private String ipRegion;

    @Schema(description = "请求参数")
    private String reqQueryParams;

    @Schema(description = "请求体")
    private String reqBody;

    @Schema(description = "返回响应体")
    private String resData;

    @Schema(description = "返回码")
    private Integer resCode;

    @Schema(description = "错误消息")
    private String errMsg;

    @Schema(description = "请求时间")
    private Date reqTime;

    @Schema(description = "操作时间")
    private Date operTime;

    @Schema(description = "消耗时间")
    private Long costTime;

}
