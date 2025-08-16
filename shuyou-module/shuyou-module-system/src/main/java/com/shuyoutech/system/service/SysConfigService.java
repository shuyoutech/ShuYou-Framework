package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import com.shuyoutech.system.domain.bo.SysConfigBo;
import com.shuyoutech.system.domain.entity.SysConfigEntity;
import com.shuyoutech.system.domain.vo.SysConfigVo;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 10:39:22
 **/
public interface SysConfigService extends SuperService<SysConfigEntity, SysConfigVo> {

    Query buildQuery(SysConfigBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<SysConfigVo> page(PageQuery<SysConfigBo> pageQuery);

    SysConfigVo detail(String id);

    String saveSysConfig(SysConfigBo bo);

    boolean updateSysConfig(SysConfigBo bo);

    boolean deleteSysConfig(List<String> ids);

}
