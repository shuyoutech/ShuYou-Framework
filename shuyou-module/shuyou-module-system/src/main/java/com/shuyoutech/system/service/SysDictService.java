package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import com.shuyoutech.system.domain.bo.SysDictBo;
import com.shuyoutech.system.domain.entity.SysDictEntity;
import com.shuyoutech.system.domain.vo.SysDictVo;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 10:17:54
 **/
public interface SysDictService extends SuperService<SysDictEntity, SysDictVo> {

    Query buildQuery(SysDictBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<SysDictVo> page(PageQuery<SysDictBo> pageQuery);

    SysDictVo detail(String id);

    String saveSysDict(SysDictBo bo);

    boolean updateSysDict(SysDictBo bo);

    boolean deleteSysDict(List<String> ids);

}
