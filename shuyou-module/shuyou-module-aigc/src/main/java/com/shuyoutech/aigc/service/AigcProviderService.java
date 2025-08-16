package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcProviderBo;
import com.shuyoutech.aigc.domain.entity.AigcProviderEntity;
import com.shuyoutech.aigc.domain.vo.AigcProviderVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.web.service.SuperService;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;

/**
 * @author YangChao
 * @date 2025-08-12 22:28:58
 **/
public interface AigcProviderService extends SuperService<AigcProviderEntity, AigcProviderVo> {

    Query buildQuery(AigcProviderBo bo);

    PageResult<AigcProviderVo> page(PageQuery<AigcProviderBo> pageQuery);

    AigcProviderVo detail(String id);

    String saveAigcProvider(AigcProviderBo bo);

    boolean updateAigcProvider(AigcProviderBo bo);

    boolean deleteAigcProvider(List<String> ids);

    Map<String, AigcProviderEntity> mapProvider();

}
