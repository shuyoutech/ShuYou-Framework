package com.shuyoutech.aigc.provider.build;

import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import dev.langchain4j.community.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiEmbeddingModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiImageModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author YangChao
 * @date 2025-05-13 15:41
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ZhipuModelBuildHandler implements ModelBuildHandler {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.ZHIPU.getValue();
    }

    @Override
    public StreamingChatModel buildStreamingModel(AigcModelEntity model) {
        return ZhipuAiStreamingChatModel.builder() //
                .apiKey(model.getApiKey()) //
                .baseUrl(model.getBaseUrl()) //
                .model(model.getModelName()) //
                .logRequests(true) //
                .logResponses(true) //
                .connectTimeout(Duration.ofMinutes(10)) //
                .readTimeout(Duration.ofMinutes(10)) //
                .build();
    }

    @Override
    public ChatModel buildChatModel(AigcModelEntity model) {
        return ZhipuAiChatModel.builder() //
                .apiKey(model.getApiKey()) //
                .baseUrl(model.getBaseUrl()) //
                .model(model.getModelName()) //
                .logRequests(true) //
                .logResponses(true) //
                .connectTimeout(Duration.ofMinutes(10)) //
                .readTimeout(Duration.ofMinutes(10)) //
                .build();
    }

    @Override
    public EmbeddingModel buildEmbeddingModel(AigcModelEntity model) {
        return ZhipuAiEmbeddingModel.builder() //
                .apiKey(model.getApiKey()) //
                .baseUrl(model.getBaseUrl()) //
                .model(model.getModelName()) //
                .logRequests(true) //
                .logResponses(true) //
                .connectTimeout(Duration.ofMinutes(10)) //
                .readTimeout(Duration.ofMinutes(10)) //
                .dimensions(1024) //
                .build();
    }

    @Override
    public ImageModel buildImageModel(AigcModelEntity model) {
        return ZhipuAiImageModel.builder() //
                .baseUrl(model.getBaseUrl()) //
                .apiKey(model.getApiKey()) //
                .model(model.getModelName()) //
                .logRequests(true) //
                .logResponses(true) //
                .connectTimeout(Duration.ofMinutes(10)) //
                .readTimeout(Duration.ofMinutes(10)) //
                .build();
    }

}
