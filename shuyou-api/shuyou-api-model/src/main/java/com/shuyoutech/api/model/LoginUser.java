package com.shuyoutech.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-07 16:58
 **/
@Data
public class LoginUser implements Serializable {

    @Schema(description = "用户ID")
    private String id;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "用户类型")
    private String userType;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "机构ID")
    private String orgId;

    @Schema(description = "机构名称")
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

    @Schema(description = "性别")
    private String sex;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "角色ID集合")
    private Set<String> roleIds;

    @Schema(description = "角色对象集合")
    private List<LoginUserRole> roles;

    @Schema(description = "岗位集合")
    private Set<String> postIds;

    @Schema(description = "岗位对象集合")
    private List<LoginUserPost> posts;

    @Schema(description = "最后登录IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    private Date loginDate;

    @Schema(description = "菜单权限")
    private Set<String> menuPermission;

    @Schema(description = "角色权限")
    private Set<String> rolePermission;

}
