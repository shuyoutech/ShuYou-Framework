package com.shuyoutech.aigc.provider;

import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import com.shuyoutech.aigc.domain.vo.AigcAppVo;
import com.shuyoutech.aigc.domain.vo.AigcKnowledgeVo;
import com.shuyoutech.aigc.enums.ModelTypeEnum;
import com.shuyoutech.aigc.provider.build.ModelBuildHandler;
import com.shuyoutech.aigc.provider.build.OpenAIModelBuildHandler;
import com.shuyoutech.aigc.provider.service.ModelService;
import com.shuyoutech.aigc.service.AigcModelService;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.SpringUtils;
import com.shuyoutech.common.core.util.StringUtils;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author YangChao
 * @date 2025-07-26 11:28
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AigcModelFactory implements CommandLineRunner, ApplicationContextAware {

    public static final Map<String, AigcModelEntity> MODEL_MAP = new ConcurrentHashMap<>();
    public static final Map<String, ModelBuildHandler> MODEL_STRATEGY_MAP = new ConcurrentHashMap<>();
    public static final Map<String, StreamingChatModel> STREAMING_CHAT_MODEL_MAP = new ConcurrentHashMap<>();
    public static final Map<String, ChatModel> CHAT_MODEL_MAP = new ConcurrentHashMap<>();
    public static final Map<String, EmbeddingModel> EMBEDDING_MODEL_MAP = new ConcurrentHashMap<>();
    public static final Map<String, ImageModel> IMAGE_MODEL_MAP = new ConcurrentHashMap<>();
    public static final MediaType MEDIA_TYPE_JSON = MediaType.get(APPLICATION_JSON_VALUE);
    public static final Map<String, ModelService> providers = new ConcurrentHashMap<>();
    private ApplicationContext applicationContext;

    public void init() {
        List<AigcModelEntity> modelList = aigcModelService.selectList();
        if (CollectionUtils.isEmpty(modelList)) {
            return;
        }
        modelList.forEach(this::buildModel);
    }

    /**
     * 获取Model服务
     */
    public static ModelService getModelService(String provider) {
        ModelService modelService = providers.get(provider.toLowerCase());
        if (null == modelService) {
            throw new BusinessException(StringUtils.format("provider:{} is not exist", provider));
        }
        return modelService;
    }

    @Override
    public void run(String... args) {
        Map<String, ModelService> beanMap = applicationContext.getBeansOfType(ModelService.class);
        for (ModelService modelService : beanMap.values()) {
            providers.put(modelService.providerName().toLowerCase(), modelService);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void buildModel(AigcModelEntity model) {
        try {
            OpenAIModelBuildHandler openAIModelBuildHandler = SpringUtils.getBean(OpenAIModelBuildHandler.class);
            ModelBuildHandler modelBuildHandler = MODEL_STRATEGY_MAP.getOrDefault(model.getProvider(), openAIModelBuildHandler);
            if (null == modelBuildHandler) {
                log.error("buildModel ========= provider:{} is not exist", model.getProvider());
                return;
            }
            if (CollectionUtils.contains(model.getModelTypes(), ModelTypeEnum.TEXT_GENERATION.getValue())) {
                StreamingChatModel streamingChatModel = modelBuildHandler.buildStreamingModel(model);
                if (null != streamingChatModel) {
                    STREAMING_CHAT_MODEL_MAP.put(model.getId(), streamingChatModel);
                    STREAMING_CHAT_MODEL_MAP.put(model.getModelName(), streamingChatModel);
                }
                ChatModel chatModel = modelBuildHandler.buildChatModel(model);
                if (null != chatModel) {
                    CHAT_MODEL_MAP.put(model.getId(), chatModel);
                }
                MODEL_MAP.put(model.getId(), model);
            } else if (CollectionUtils.contains(model.getModelTypes(), ModelTypeEnum.TEXT_EMBEDDING.getValue())) {
                EmbeddingModel embeddingModel = modelBuildHandler.buildEmbeddingModel(model);
                if (null != embeddingModel) {
                    EMBEDDING_MODEL_MAP.put(model.getId(), embeddingModel);
                }
                MODEL_MAP.put(model.getId(), model);
            } else if (CollectionUtils.contains(model.getModelTypes(), ModelTypeEnum.IMAGE_GENERATION.getValue())) {
                ImageModel imageModel = modelBuildHandler.buildImageModel(model);
                if (null != imageModel) {
                    IMAGE_MODEL_MAP.put(model.getId(), imageModel);
                }
                MODEL_MAP.put(model.getId(), model);
            }
        } catch (Exception e) {
            log.error("buildModel id: {} name: {} 配置报错, 错误信息:{}", model.getId(), model.getModelName(), e.getMessage());
        }
    }

    public EmbeddingModel getEmbeddingModelByKnowledgeId(String knowledgeId) {
        AigcKnowledgeVo knowledge = aigcKnowledgeFactory.getKnowledge(knowledgeId);
        if (null == knowledge) {
            throw new BusinessException("没有找到匹配的向量模型");
        }
        String embeddingModelId = knowledge.getEmbeddingModelId();
        return getEmbeddingModel(embeddingModelId);
    }

    public StreamingChatModel getStreamingChatModelByAppId(String appId) {
        AigcAppVo app = aigcAppFactory.getApp(appId);
        if (null == app) {
            throw new BusinessException("没有找到匹配的对话模型");
        }
        String chatModelId = app.getChatModelId();
        return getStreamingChatModel(chatModelId);
    }

    public StreamingChatModel getStreamingChatModel(String modelId) {
        return STREAMING_CHAT_MODEL_MAP.get(modelId);
    }

    public ChatModel getChatModel(String modelId) {
        return CHAT_MODEL_MAP.get(modelId);
    }

    public EmbeddingModel getEmbeddingModel(String modelId) {
        return EMBEDDING_MODEL_MAP.get(modelId);
    }

    public ImageModel getImageModel(String modelId) {
        return IMAGE_MODEL_MAP.get(modelId);
    }

    private final AigcModelService aigcModelService;
    private final AigcKnowledgeFactory aigcKnowledgeFactory;
    private final AigcAppFactory aigcAppFactory;

}
