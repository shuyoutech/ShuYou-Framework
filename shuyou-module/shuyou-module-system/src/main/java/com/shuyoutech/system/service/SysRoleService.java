package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import com.shuyoutech.system.domain.bo.SysRoleBo;
import com.shuyoutech.system.domain.entity.SysRoleEntity;
import com.shuyoutech.system.domain.vo.SysRoleVo;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 20:09
 **/
public interface SysRoleService extends SuperService<SysRoleEntity, SysRoleVo> {

    Query buildQuery(SysRoleBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<SysRoleVo> page(PageQuery<SysRoleBo> pageQuery);

    SysRoleVo detail(String id);

    String saveRole(SysRoleBo bo);

    boolean updateRole(SysRoleBo bo);

    boolean deleteRole(List<String> ids);

    boolean statusRole(String id, String status);

}
