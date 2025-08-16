package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import com.shuyoutech.system.domain.bo.SysDictTypeBo;
import com.shuyoutech.system.domain.entity.SysDictTypeEntity;
import com.shuyoutech.system.domain.vo.SysDictTypeVo;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 09:55:16
 **/
public interface SysDictTypeService extends SuperService<SysDictTypeEntity, SysDictTypeVo> {

    Query buildQuery(SysDictTypeBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<SysDictTypeVo> page(PageQuery<SysDictTypeBo> pageQuery);

    SysDictTypeVo detail(String id);

    String saveSysDictType(SysDictTypeBo bo);

    boolean updateSysDictType(SysDictTypeBo bo);

    boolean deleteSysDictType(List<String> ids);

}
