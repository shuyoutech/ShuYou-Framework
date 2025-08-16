package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import com.shuyoutech.system.domain.bo.SysUserBusinessBo;
import com.shuyoutech.system.domain.entity.SysUserBusinessEntity;
import com.shuyoutech.system.domain.vo.SysUserBusinessVo;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-10 13:35:30
 **/
public interface SysUserBusinessService extends SuperService<SysUserBusinessEntity, SysUserBusinessVo> {

    Query buildQuery(SysUserBusinessBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<SysUserBusinessVo> page(PageQuery<SysUserBusinessBo> pageQuery);

    SysUserBusinessVo detail(String id);

    String saveSysUserBusiness(SysUserBusinessBo bo);

    boolean updateSysUserBusiness(SysUserBusinessBo bo);

    boolean deleteSysUserBusiness(List<String> ids);

}
