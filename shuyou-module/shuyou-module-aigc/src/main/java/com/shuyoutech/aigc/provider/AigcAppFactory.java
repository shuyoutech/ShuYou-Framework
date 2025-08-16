package com.shuyoutech.aigc.provider;

import com.shuyoutech.aigc.domain.entity.AigcAppEntity;
import com.shuyoutech.aigc.domain.vo.AigcAppVo;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.mongodb.MongoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.shuyoutech.aigc.provider.AigcModelFactory.MODEL_MAP;

/**
 * @author YangChao
 * @date 2025-05-17 11:25
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AigcAppFactory {

    public static final Map<String, AigcAppVo> APP_MAP = new ConcurrentHashMap<>();

    public void init() {
        List<AigcAppEntity> appList = MongoUtils.selectList(AigcAppEntity.class);
        if (CollectionUtils.isEmpty(appList)) {
            return;
        }
        appList.forEach(this::buildApp);
    }

    public void buildApp(AigcAppEntity app) {
        AigcAppVo vo = MapstructUtils.convert(app, AigcAppVo.class);
        APP_MAP.put(vo.getId(), vo);
    }

    public AigcAppVo getApp(String appId) {
        AigcAppVo app = APP_MAP.get(appId);
        if (null == app) {
            return null;
        }
        if (StringUtils.isNotBlank(app.getKnowledgeId())) {
            app.setKnowledge(aigcKnowledgeFactory.getKnowledge(app.getKnowledgeId()));
        }
        if (StringUtils.isNotBlank(app.getChatModelId())) {
            app.setChatModel(MODEL_MAP.get(app.getChatModelId()));
        }
        return app;
    }

    private final AigcKnowledgeFactory aigcKnowledgeFactory;
}
