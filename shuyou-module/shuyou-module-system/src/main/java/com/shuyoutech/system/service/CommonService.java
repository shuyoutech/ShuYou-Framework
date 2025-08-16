package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.system.domain.bo.RoleMenuBo;
import com.shuyoutech.system.domain.bo.RoleUserBo;
import com.shuyoutech.system.domain.bo.RoleUserListBo;
import com.shuyoutech.system.domain.bo.SysUserBo;
import com.shuyoutech.system.domain.vo.SysUserVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author YangChao
 * @date 2025-07-04 15:32
 **/
public interface CommonService {

    void templateFile(String templateType, HttpServletRequest request, HttpServletResponse response);

    PageResult<SysUserVo> grantUserList(PageQuery<SysUserBo> pageQuery);

    PageResult<SysUserVo> unGrantUserList(PageQuery<SysUserBo> pageQuery);

    void cancelGrantUser(RoleUserBo bo);

    void batchCancelGrantUser(RoleUserListBo bo);

    void batchGrantUser(RoleUserListBo bo);

    void grantMenu(RoleMenuBo bo);

}
