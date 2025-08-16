package com.shuyoutech.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-01 19:45
 **/
@Data
@Schema(description = "个人信息类")
public class ProfileVo implements Serializable {

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "机构ID")
    private String orgId;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "姓名")
    private String realName;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户类型")
    private String userType;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "电子邮箱")
    private String email;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "性别:0-男,1-女,2-未知")
    private String gender;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "角色集合")
    private Set<String> roleIds;

    @Schema(description = "角色名称")
    private String roleNames;

    @Schema(description = "岗位集合")
    private Set<String> postIds;

    @Schema(description = "岗位名称")
    private String postNames;

}
