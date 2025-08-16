package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import com.shuyoutech.system.domain.bo.SysPostBo;
import com.shuyoutech.system.domain.entity.SysPostEntity;
import com.shuyoutech.system.domain.vo.SysPostVo;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 10:33:09
 **/
public interface SysPostService extends SuperService<SysPostEntity, SysPostVo> {

    Query buildQuery(SysPostBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<SysPostVo> page(PageQuery<SysPostBo> pageQuery);

    SysPostVo detail(String id);

    String saveSysPost(SysPostBo bo);

    boolean updateSysPost(SysPostBo bo);

    boolean deleteSysPost(List<String> ids);

    boolean statusSysPost(String id, String status);

}
