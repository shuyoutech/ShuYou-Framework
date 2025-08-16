package com.shuyoutech.system.service;

import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StreamUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.satoken.util.AuthUtils;
import com.shuyoutech.system.domain.entity.SysMenuEntity;
import com.shuyoutech.system.domain.entity.SysRoleEntity;
import com.shuyoutech.system.domain.entity.SysUserEntity;
import com.shuyoutech.system.domain.vo.ProfileUpdateVo;
import com.shuyoutech.system.domain.vo.ProfileVo;
import com.shuyoutech.system.domain.vo.SysFileVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-01 19:43
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysProfileServiceImpl implements SysProfileService {

    @Override
    public ProfileVo getProfile() {
        String userId = AuthUtils.getLoginUserId();
        SysUserEntity user = sysUserService.getById(userId);
        if (null == user) {
            log.error("getProfile ========== userId:{} is not exist", userId);
            throw new BusinessException("获取用户信息失败");
        }
        ProfileVo profile = MapstructUtils.convert(user, ProfileVo.class);
        Set<String> roleIds = user.getRoleIds();
        if (null != profile) {
            List<SysRoleEntity> roles = sysRoleService.getByIds(roleIds);
            profile.setRoleNames(CollectionUtils.join(StreamUtils.toList(roles, SysRoleEntity::getRoleName), ","));
            if (StringUtils.isNotBlank(user.getAvatar())) {
                profile.setAvatar(sysFileService.generatedUrl(user.getAvatar(), 86400000L));
            }
        }
        return profile;
    }

    @Override
    public void updateProfile(ProfileUpdateVo profile) {
        String userId = AuthUtils.getLoginUserId();
        Update update = new Update();
        update.set("nickname", profile.getNickname());
        update.set("mobile", profile.getMobile());
        update.set("email", profile.getEmail());
        update.set("gender", profile.getGender());
        update.set("address", profile.getAddress());
        MongoUtils.patch(userId, update, SysUserEntity.class);
    }

    @Override
    public void updatePassword(String userId, String oldPassword, String newPassword) {
        SysUserEntity user = sysUserService.getById(userId);
        if (null == user) {
            log.error("updatePassword ========== userId:{} is not exist", userId);
            throw new BusinessException(StringUtils.format("用户id:{}不存在", userId));
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("用户密码不正确!");
        }
        Update update = new Update();
        update.set("password", passwordEncoder.encode(newPassword));
        MongoUtils.patch(userId, update, SysUserEntity.class);
    }

    @Override
    public String avatar(MultipartFile file) {
        SysFileVo sysFile = sysFileService.upload(file);
        if (null == sysFile) {
            throw new BusinessException("上传头像失败");
        }
        String userId = AuthUtils.getLoginUserId();
        Update update = new Update();
        update.set("avatar", sysFile.getId());
        MongoUtils.patch(userId, update, SysUserEntity.class);
        return sysFile.getPreviewUrl();
    }

    @Override
    public Set<String> permission(String userId) {
        Set<String> result = CollectionUtils.newHashSet();
        SysUserEntity user = sysUserService.getById(userId);
        if (null == user || CollectionUtils.isEmpty(user.getRoleIds())) {
            return result;
        }
        Set<String> roleIds = user.getRoleIds();
        List<SysRoleEntity> roles = sysRoleService.getByIds(roleIds);
        if (CollectionUtils.isEmpty(roles)) {
            return result;
        }
        Set<String> menuIds = CollectionUtils.newHashSet();
        roles.forEach(role -> {
            if (CollectionUtils.isNotEmpty(role.getMenuIds())) {
                menuIds.addAll(role.getMenuIds());
            }
        });
        List<SysMenuEntity> menus = sysMenuService.getByIds(menuIds);
        menus.forEach(menu -> result.add(menu.getPerms()));
        return result;
    }

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final PasswordEncoder passwordEncoder;
    private final SysMenuService sysMenuService;
    private final SysFileService sysFileService;

}
