package com.shuyoutech.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YangChao
 * @date 2025-07-01 19:45
 **/
@Data
@Schema(description = "个人信息修改类")
public class ProfileUpdateVo implements Serializable {

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "电子邮箱")
    private String email;

    @Schema(description = "性别:0-男,1-女,2-未知")
    private String gender;

    @Schema(description = "地址")
    private String address;

}
