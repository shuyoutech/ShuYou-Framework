package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.AigcModelBo;
import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import com.shuyoutech.aigc.domain.vo.AigcModelVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.web.service.SuperService;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-11 11:18:54
 **/
public interface AigcModelService extends SuperService<AigcModelEntity, AigcModelVo> {

    Query buildQuery(AigcModelBo bo);

    boolean checkUnique(ParamUnique paramUnique);

    PageResult<AigcModelVo> page(PageQuery<AigcModelBo> pageQuery);

    AigcModelVo detail(String id);

    String saveAiModel(AigcModelBo bo);

    boolean updateAiModel(AigcModelBo bo);

    boolean deleteAiModel(List<String> ids);

    boolean statusAiModel(String id, String status);

    AigcModelEntity getModel(String provider, String modelName);

}
