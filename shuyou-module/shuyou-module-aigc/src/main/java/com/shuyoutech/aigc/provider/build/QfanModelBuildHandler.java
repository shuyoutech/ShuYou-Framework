package com.shuyoutech.aigc.provider.build;

import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import dev.langchain4j.community.model.qianfan.QianfanChatModel;
import dev.langchain4j.community.model.qianfan.QianfanEmbeddingModel;
import dev.langchain4j.community.model.qianfan.QianfanStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author YangChao
 * @date 2025-05-13 15:41
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class QfanModelBuildHandler implements ModelBuildHandler {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.BAIDU.getValue();
    }

    @Override
    public StreamingChatModel buildStreamingModel(AigcModelEntity model) {
        return QianfanStreamingChatModel.builder() //
                .baseUrl(model.getBaseUrl()) //
                .apiKey(model.getApiKey()) //
                .modelName(model.getModelName()) //
                .logRequests(true) //
                .logResponses(true) //
                .build();
    }

    @Override
    public ChatModel buildChatModel(AigcModelEntity model) {
        return QianfanChatModel.builder() //
                .baseUrl(model.getBaseUrl()) //
                .apiKey(model.getApiKey()) //
                .modelName(model.getModelName()) //
                .logRequests(true) //
                .logResponses(true) //
                .build();
    }

    @Override
    public EmbeddingModel buildEmbeddingModel(AigcModelEntity model) {
        return QianfanEmbeddingModel.builder() //
                .apiKey(model.getApiKey()) //
                .modelName(model.getModelName()) //
                .logRequests(true) //
                .logResponses(true) //
                .build();
    }

    @Override
    public ImageModel buildImageModel(AigcModelEntity model) {
        return null;
    }

}
