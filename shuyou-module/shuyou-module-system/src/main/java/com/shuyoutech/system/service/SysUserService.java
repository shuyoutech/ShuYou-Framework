package com.shuyoutech.system.service;

import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import com.shuyoutech.system.domain.bo.SysUserBo;
import com.shuyoutech.system.domain.entity.SysUserEntity;
import com.shuyoutech.system.domain.vo.SysUserVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * @author YangChao
 * @date 2025-02-14 16:18:57
 **/
public interface SysUserService extends SuperService<SysUserEntity, SysUserVo> {

    Query buildQuery(SysUserBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<SysUserVo> page(PageQuery<SysUserBo> pageQuery);

    SysUserVo detail(String id);

    String saveSysUser(SysUserBo bo);

    boolean updateSysUser(SysUserBo bo);

    boolean deleteSysUser(List<String> ids);

    boolean statusSysUser(String id, String status);

    Boolean passwordMatch(String rawPassword, String encodedPassword);

    JSONObject importUser(MultipartFile file, HttpServletRequest request);

    void export(SysUserBo query, HttpServletRequest request, HttpServletResponse response);

    void resetPassword(String userId, String password);

    void grantRole(String userId, Set<String> roleIds);

    void grantPost(String userId, Set<String> postIds);

}
