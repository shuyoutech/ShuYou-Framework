package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.web.service.SuperService;
import com.shuyoutech.system.domain.entity.SysLogEntity;
import com.shuyoutech.system.domain.vo.SysLogQuery;
import com.shuyoutech.system.domain.vo.SysLogVo;

/**
 * @author YangChao
 * @date 2025-07-07 20:09
 **/
public interface SysLogService extends SuperService<SysLogEntity, SysLogVo> {

    PageResult<SysLogVo> page(PageQuery<SysLogQuery> pageQuery);

    SysLogVo detail(String id);

}
