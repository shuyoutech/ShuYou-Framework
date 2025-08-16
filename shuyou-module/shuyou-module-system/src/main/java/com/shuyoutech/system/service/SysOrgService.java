package com.shuyoutech.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperTreeService;
import com.shuyoutech.system.domain.bo.SysOrgBo;
import com.shuyoutech.system.domain.entity.SysOrgEntity;
import com.shuyoutech.system.domain.vo.SysOrgVo;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 09:17:49
 **/
public interface SysOrgService extends SuperTreeService<SysOrgEntity, SysOrgVo> {

    Query buildQuery(SysOrgBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<SysOrgVo> page(PageQuery<SysOrgBo> pageQuery);

    SysOrgVo detail(String id);

    String saveSysOrg(SysOrgBo bo);

    boolean updateSysOrg(SysOrgBo bo);

    boolean deleteSysOrg(List<String> ids);

    boolean statusSysOrg(String id, String status);

    List<Tree<String>> tree(SysOrgBo bo);

}
