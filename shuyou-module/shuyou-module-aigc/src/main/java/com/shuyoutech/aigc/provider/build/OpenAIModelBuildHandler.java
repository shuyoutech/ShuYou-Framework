package com.shuyoutech.aigc.provider.build;

import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
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
public class OpenAIModelBuildHandler implements ModelBuildHandler {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.OPENAI.getValue();
    }

    @Override
    public StreamingChatModel buildStreamingModel(AigcModelEntity model) {
        return OpenAiStreamingChatModel.builder() //
                .apiKey(model.getApiKey()) //
                .baseUrl(model.getBaseUrl()) //
                .modelName(model.getModelName()) //
                .logRequests(true) //
                .logResponses(true) //
                .timeout(Duration.ofMinutes(10)) //
                .build();
    }

    @Override
    public ChatModel buildChatModel(AigcModelEntity model) {
        return OpenAiChatModel.builder() //
                .apiKey(model.getApiKey()) //
                .baseUrl(model.getBaseUrl()) //
                .modelName(model.getModelName()) //
                .logRequests(true) //
                .logResponses(true) //
                .timeout(Duration.ofMinutes(10)) //
                .build();
    }

    @Override
    public EmbeddingModel buildEmbeddingModel(AigcModelEntity model) {
        return OpenAiEmbeddingModel.builder() //
                .apiKey(model.getApiKey()) //
                .baseUrl(model.getBaseUrl()) //
                .modelName(model.getModelName()) //
                .dimensions(1024) //
                .logRequests(true) //
                .logResponses(true) //
                .dimensions(1024) //
                .timeout(Duration.ofMinutes(10)) //
                .build();
    }

    @Override
    public ImageModel buildImageModel(AigcModelEntity model) {
        return OpenAiImageModel.builder() //
                .apiKey(model.getApiKey()) //
                .baseUrl(model.getBaseUrl()) //
                .modelName(model.getModelName()) //
                .logRequests(true) //
                .logResponses(true) //
                .timeout(Duration.ofMinutes(10)) //
                .build();
    }

}
