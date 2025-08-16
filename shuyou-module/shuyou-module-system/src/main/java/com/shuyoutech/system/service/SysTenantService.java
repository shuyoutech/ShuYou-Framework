package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import com.shuyoutech.system.domain.bo.SysTenantBo;
import com.shuyoutech.system.domain.entity.SysTenantEntity;
import com.shuyoutech.system.domain.vo.SysTenantVo;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 09:01:15
 **/
public interface SysTenantService extends SuperService<SysTenantEntity, SysTenantVo> {

    Query buildQuery(SysTenantBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<SysTenantVo> page(PageQuery<SysTenantBo> pageQuery);

    SysTenantVo detail(String id);

    String saveSysTenant(SysTenantBo bo);

    boolean updateSysTenant(SysTenantBo bo);

    boolean deleteSysTenant(List<String> ids);

    boolean statusSysTenant(String id, String status);

}
