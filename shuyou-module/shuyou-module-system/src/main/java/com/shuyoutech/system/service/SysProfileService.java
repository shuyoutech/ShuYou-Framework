package com.shuyoutech.system.service;

import com.shuyoutech.system.domain.vo.ProfileUpdateVo;
import com.shuyoutech.system.domain.vo.ProfileVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * @author YangChao
 * @date 2025-07-01 19:43
 **/
public interface SysProfileService {

    ProfileVo getProfile();

    void updateProfile(ProfileUpdateVo profile);

    void updatePassword(String userId, String oldPassword, String newPassword);

    String avatar(MultipartFile file);

    Set<String> permission(String userId);

}
