package com.shuyoutech.system.service;

import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import com.shuyoutech.system.domain.bo.SysNoticeBo;
import com.shuyoutech.system.domain.entity.SysNoticeEntity;
import com.shuyoutech.system.domain.vo.SysNoticeVo;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-07 10:55:00
 **/
public interface SysNoticeService extends SuperService<SysNoticeEntity, SysNoticeVo> {

    Query buildQuery(SysNoticeBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<SysNoticeVo> page(PageQuery<SysNoticeBo> pageQuery);

    SysNoticeVo detail(String id);

    String saveSysNotice(SysNoticeBo bo);

    boolean updateSysNotice(SysNoticeBo bo);

    boolean deleteSysNotice(List<String> ids);

    boolean statusSysNotice(String id, String status);

}
