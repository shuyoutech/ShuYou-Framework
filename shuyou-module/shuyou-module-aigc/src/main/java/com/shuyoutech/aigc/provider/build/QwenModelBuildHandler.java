package com.shuyoutech.aigc.provider.build;

import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
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
public class QwenModelBuildHandler implements ModelBuildHandler {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.ALIYUN.getValue();
    }

    @Override
    public StreamingChatModel buildStreamingModel(AigcModelEntity model) {
        return QwenStreamingChatModel.builder() //
                .apiKey(model.getApiKey()) //
                .modelName(model.getModelName()) //
                .build();
    }

    @Override
    public ChatModel buildChatModel(AigcModelEntity model) {
        return QwenChatModel.builder() //
                .apiKey(model.getApiKey()) //
                .modelName(model.getModelName()) //
                .enableSearch(true) //
                .build();
    }

    @Override
    public EmbeddingModel buildEmbeddingModel(AigcModelEntity model) {
        return QwenEmbeddingModel.builder() //
                .apiKey(model.getApiKey()) //
                .modelName(model.getModelName()) //
                .build();
    }

    @Override
    public ImageModel buildImageModel(AigcModelEntity model) {
        return null;
    }

}
