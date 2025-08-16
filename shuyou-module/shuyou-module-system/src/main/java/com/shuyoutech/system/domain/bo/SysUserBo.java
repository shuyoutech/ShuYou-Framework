package com.shuyoutech.system.domain.bo;

import com.shuyoutech.common.core.enums.SexEnum;
import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.core.model.StatusGroup;
import com.shuyoutech.common.core.model.UpdateGroup;
import com.shuyoutech.system.domain.entity.SysUserEntity;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-07 09:32:59
 **/
@Data
@AutoMapper(target = SysUserEntity.class)
@Schema(description = "用户类")
public class SysUserBo implements Serializable {

    @NotBlank(message = "id不能为空", groups = {UpdateGroup.class, StatusGroup.class})
    @Schema(description = "主键")
    private String id;

    /**
     * 枚举 {@link StatusEnum}
     */
    @NotNull(message = "状态不能为空", groups = {StatusGroup.class})
    @Schema(description = "状态")
    private String status;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "机构ID")
    private String orgId;

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
    @Schema(description = "性别")
    private String sex;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "角色ID")
    private String roleId;

    @Schema(description = "不为角色ID")
    private String neRoleId;

    @Schema(description = "角色集合")
    private Set<String> roleIds;

    @Schema(description = "岗位集合")
    private Set<String> postIds;

    @Schema(description = "最后登录IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    private Date loginDate;

    @Schema(description = "机构ID")
    private List<String> orgIds;

    @Schema(description = "起始时间")
    private String startDate;

    @Schema(description = "结束时间")
    private String endDate;

}
