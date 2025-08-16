package com.shuyoutech.aigc.service;

import cn.hutool.core.util.DesensitizedUtil;
import com.shuyoutech.aigc.domain.bo.AigcModelBo;
import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import com.shuyoutech.aigc.domain.entity.AigcModelPrice;
import com.shuyoutech.aigc.domain.entity.AigcProviderEntity;
import com.shuyoutech.aigc.domain.vo.AigcModelPriceVo;
import com.shuyoutech.aigc.domain.vo.AigcModelVo;
import com.shuyoutech.api.service.RemoteSystemService;
import com.shuyoutech.common.core.enums.DictTypeEnum;
import com.shuyoutech.common.core.enums.StatusEnum;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.*;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author YangChao
 * @date 2025-07-11 11:18:54
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AigcModelServiceImpl extends SuperServiceImpl<AigcModelEntity, AigcModelVo> implements AigcModelService {

    @Override
    public List<AigcModelVo> convertTo(List<AigcModelEntity> list) {
        List<AigcModelVo> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        Map<String, String> statusMap = remoteSystemService.translateByDictCode(DictTypeEnum.STATUS_TYPE.getValue());
        Map<String, String> modelMap = remoteSystemService.translateByDictCode(DictTypeEnum.MODEL_TYPE.getValue());
        Map<String, String> chargeMap = remoteSystemService.translateByDictCode(DictTypeEnum.AI_CHARGE_TYPE.getValue());
        Map<String, String> priceUnitMap = remoteSystemService.translateByDictCode(DictTypeEnum.AI_TOKEN_PRICE_UNIT.getValue());
        Map<String, String> currencyMap = remoteSystemService.translateByDictCode(DictTypeEnum.CURRENCY_UNIT_TYPE.getValue());
        Map<String, String> ruleMap = remoteSystemService.translateByDictCode(DictTypeEnum.AI_FEE_RULE.getValue());
        Map<String, AigcProviderEntity> providerMap = aigcProviderService.mapProvider();
        list.forEach(e -> {
            AigcModelVo vo = MapstructUtils.convert(e, this.voClass);
            if (null != vo) {
                vo.setStatusName(statusMap.getOrDefault(e.getStatus(), ""));
                vo.setProviderName(providerMap.containsKey(e.getProvider()) ? providerMap.get(e.getProvider()).getProviderName() : "");
                vo.setProviderIcon(providerMap.containsKey(e.getProvider()) ? providerMap.get(e.getProvider()).getProviderIcon() : "");
                vo.setModelTypeNames(CollectionUtils.translate(vo.getModelTypes(), modelMap));
                vo.setApiKey(DesensitizedUtil.password(e.getApiKey()));
                vo.setChargeTypeName(chargeMap.getOrDefault(e.getChargeType(), ""));
                if (CollectionUtils.isNotEmpty(e.getPrices())) {
                    List<AigcModelPriceVo> prices = CollectionUtils.newArrayList();
                    for (AigcModelPrice price : e.getPrices()) {
                        AigcModelPriceVo priceVo = MapstructUtils.convert(price, AigcModelPriceVo.class);
                        priceVo.setFeeRuleName(MapUtils.getStr(ruleMap, price.getFeeRule(), ""));
                        priceVo.setTokenPriceUnitName(priceUnitMap.getOrDefault(price.getTokenPriceUnit(), ""));
                        priceVo.setCurrencyUnitName(currencyMap.getOrDefault(price.getCurrencyUnit(), ""));
                        prices.add(priceVo);
                    }
                    vo.setPrices(prices);
                }
                result.add(vo);
            }
        });
        return result;
    }

    public AigcModelVo convertTo(AigcModelEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(AigcModelBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getProvider())) {
            query.addCriteria(Criteria.where("provider").is(bo.getProvider()));
        }
        if (StringUtils.isNotBlank(bo.getModelName())) {
            query.addCriteria(Criteria.where("modelName").regex(Pattern.compile(String.format("^.*%s.*$", bo.getModelName()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getModelType())) {
            query.addCriteria(Criteria.where("modelTypes").is(bo.getModelType()));
        }
        if (BooleanUtils.isTrue(bo.getBeenHot())) {
            query.addCriteria(Criteria.where("beenHot").is(true));
        }
        if (StringUtils.isNotBlank(bo.getChargeType())) {
            query.addCriteria(Criteria.where("chargeType").is(bo.getChargeType()));
        }
        if (StringUtils.isNotBlank(bo.getModelTag())) {
            query.addCriteria(Criteria.where("modelTags").is(bo.getModelTag()));
        }
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        AigcModelEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<AigcModelVo> page(PageQuery<AigcModelBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public AigcModelVo detail(String id) {
        AigcModelEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveAiModel(AigcModelBo bo) {
        bo.setStatus(StringUtils.isBlank(bo.getStatus()) ? StatusEnum.ENABLE.getValue() : bo.getStatus());
        AigcModelEntity entity = this.save(bo);
        return entity.getId();
    }

    @Override
    public boolean updateAiModel(AigcModelBo bo) {
        return this.patch(bo);
    }

    @Override
    public boolean deleteAiModel(List<String> ids) {
        return this.deleteByIds(ids);
    }

    @Override
    public boolean statusAiModel(String id, String status) {
        AigcModelEntity entity = this.getById(id);
        if (null == entity) {
            return false;
        }
        Update update = new Update();
        update.set("status", status);
        return MongoUtils.patch(id, update, AigcModelEntity.class);
    }

    @Override
    public AigcModelEntity getModel(String provider, String modelName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("provider").is(provider));
        query.addCriteria(Criteria.where("modelName").is(modelName));
        return MongoUtils.selectOne(query, AigcModelEntity.class);
    }

    private final RemoteSystemService remoteSystemService;
    private final AigcProviderService aigcProviderService;

}