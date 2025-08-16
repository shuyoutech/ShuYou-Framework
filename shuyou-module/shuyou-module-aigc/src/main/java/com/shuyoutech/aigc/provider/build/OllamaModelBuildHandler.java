package com.shuyoutech.aigc.provider.build;

import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
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
public class OllamaModelBuildHandler implements ModelBuildHandler {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.OLLAMA.getValue();
    }

    @Override
    public StreamingChatModel buildStreamingModel(AigcModelEntity model) {
        return OllamaStreamingChatModel.builder() //
                .baseUrl(model.getBaseUrl()) //
                .modelName(model.getModelName()) //
                .logRequests(true) //
                .logResponses(true) //
                .timeout(Duration.ofMinutes(10)) //
                .build();
    }

    @Override
    public ChatModel buildChatModel(AigcModelEntity model) {
        return OllamaChatModel.builder() //
                .baseUrl(model.getBaseUrl()) //
                .modelName(model.getModelName()) //
                .logRequests(true) //
                .logResponses(true) //
                .timeout(Duration.ofMinutes(10)) //
                .build();
    }

    @Override
    public EmbeddingModel buildEmbeddingModel(AigcModelEntity model) {
        return OllamaEmbeddingModel.builder() //
                .baseUrl(model.getBaseUrl()) //
                .modelName(model.getModelName()) //
                .logRequests(true) //
                .logResponses(true) //
                .timeout(Duration.ofMinutes(10)) //
                .build();
    }

    @Override
    public ImageModel buildImageModel(AigcModelEntity model) {
        return null;
    }

}
