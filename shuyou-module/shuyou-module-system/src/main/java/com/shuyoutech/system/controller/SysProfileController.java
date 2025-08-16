package com.shuyoutech.system.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import com.shuyoutech.common.core.constant.MimeTypeConstants;
import com.shuyoutech.common.core.model.R;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.satoken.util.AuthUtils;
import com.shuyoutech.system.domain.vo.ProfileUpdatePasswordVo;
import com.shuyoutech.system.domain.vo.ProfileUpdateVo;
import com.shuyoutech.system.domain.vo.ProfileVo;
import com.shuyoutech.system.service.SysProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Set;

import static com.shuyoutech.common.core.model.R.error;
import static com.shuyoutech.common.core.model.R.success;

/**
 * @author YangChao
 * @date 2025-07-01 19:28
 **/
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
@Tag(name = "SysProfileController", description = "个人信息业务处理API控制器")
public class SysProfileController {

    @PostMapping("getProfile")
    @Operation(summary = "获取个人信息")
    public R<ProfileVo> getProfile() {
        return R.success(sysProfileService.getProfile());
    }

    @PostMapping(path = "updateProfile")
    @Operation(description = "修改用户信息")
    public R<Void> updateProfile(@RequestBody ProfileUpdateVo profile) {
        sysProfileService.updateProfile(profile);
        return R.success();
    }

    @PostMapping(path = "updatePassword")
    @Operation(description = "修改密码")
    public R<Void> updatePassword(@RequestBody ProfileUpdatePasswordVo updatePwd) {
        String userId = AuthUtils.getLoginUserId();
        String newPassword = updatePwd.getNewPassword();
        String oldPassword = updatePwd.getOldPassword();
        sysProfileService.updatePassword(userId, oldPassword, newPassword);
        return R.success();
    }

    @PostMapping("avatar")
    @Operation(description = "上传用户头像")
    public R<String> avatar(@RequestParam("file") MultipartFile file) {
        try {
            if (null == file) {
                return error("上传文件不能为空");
            }
            String fileType = FileUtil.extName(file.getOriginalFilename());
            if (!ArrayUtil.contains(MimeTypeConstants.IMAGE_EXTENSION, fileType)) {
                return error(StringUtils.format("文件格式不正确，请上传{}格式", Arrays.toString(MimeTypeConstants.IMAGE_EXTENSION)));
            }
            return success(sysProfileService.avatar(file));
        } catch (Exception e) {
            log.error("上传用户头像异常 ============ exception:{}", e.getMessage());
        }
        return error("上传用户头像失败");
    }

    @PostMapping("permission")
    @Operation(description = "获取路由权限")
    public R<Set<String>> permission() {
        String userId = AuthUtils.getLoginUserId();
        return R.success(sysProfileService.permission(userId));
    }

    private final SysProfileService sysProfileService;
}
