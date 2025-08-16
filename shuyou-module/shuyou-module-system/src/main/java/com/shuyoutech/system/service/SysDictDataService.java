package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import com.shuyoutech.system.domain.bo.SysDictDataBo;
import com.shuyoutech.system.domain.entity.SysDictDataEntity;
import com.shuyoutech.system.domain.vo.SysDictDataVo;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 10:02:47
 **/
public interface SysDictDataService extends SuperService<SysDictDataEntity, SysDictDataVo> {

    Query buildQuery(SysDictDataBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<SysDictDataVo> page(PageQuery<SysDictDataBo> pageQuery);

    SysDictDataVo detail(String id);

    String saveSysDictData(SysDictDataBo bo);

    boolean updateSysDictData(SysDictDataBo bo);

    boolean deleteSysDictData(List<String> ids);

}
