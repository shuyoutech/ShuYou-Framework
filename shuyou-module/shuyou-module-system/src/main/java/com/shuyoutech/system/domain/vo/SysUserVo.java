package com.shuyoutech.system.domain.vo;

import com.shuyoutech.common.core.enums.SexEnum;
import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.mongodb.model.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-02-14 16:18:57
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户显示类")
public class SysUserVo extends BaseVo {

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "创建者ID")
    private String createUserId;

    @Schema(description = "组织机构ID")
    private String createOrgId;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "更新者ID")
    private String updateUserId;

    /**
     * 枚举 {@link StatusEnum}
     */
    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "组织机构ID")
    private String orgId;

    @Schema(description = "组织机构名称")
    private String orgName;

    @Schema(description = "姓名")
    private String realName;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "电子邮箱")
    private String email;

    @Schema(description = "用户头像")
    private String avatar;

    /**
     * 枚举 {@link SexEnum}
     */
    @Schema(description = "性别:male-男,female-女,unknown-未知")
    private String sex;

    @Schema(description = "性别名称")
    private String sexName;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "角色集合")
    private Set<String> roleIds;

    @Schema(description = "角色名称集合")
    private List<String> roleNames;

    @Schema(description = "岗位集合")
    private Set<String> postIds;

    @Schema(description = "岗位名称集合")
    private List<String> postNames;

    @Schema(description = "最后登录IP")
    private String loginIp;

    @Schema(description = "最后登录地址")
    private String loginRegion;

    @Schema(description = "最后登录时间")
    private Date loginDate;

}
