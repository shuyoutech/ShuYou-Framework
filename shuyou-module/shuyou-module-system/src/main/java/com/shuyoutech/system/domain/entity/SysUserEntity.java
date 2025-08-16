package com.shuyoutech.system.domain.entity;

import com.shuyoutech.api.model.LoginUser;
import com.shuyoutech.common.core.enums.SexEnum;
import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.mongodb.model.BaseEntity;
import com.shuyoutech.system.domain.vo.ProfileVo;
import com.shuyoutech.system.domain.vo.SysUserVo;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-06-11 09:55
 **/
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AutoMappers({@AutoMapper(target = SysUserVo.class), @AutoMapper(target = LoginUser.class), @AutoMapper(target = ProfileVo.class)})
@Schema(description = "用户表")
@Document(collection = "sys_user")
public class SysUserEntity extends BaseEntity<SysUserEntity> {

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

    @Schema(description = "角色集合")
    private Set<String> roleIds;

    @Schema(description = "岗位集合")
    private Set<String> postIds;

    @Schema(description = "最后登录IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    private Date loginDate;

}
