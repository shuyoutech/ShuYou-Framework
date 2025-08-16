package com.shuyoutech.aigc.provider.build;

import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;

/**
 * @author YangChao
 * @date 2025-05-13 15:40
 **/
public interface ModelBuildHandler {

    String providerName();

    StreamingChatModel buildStreamingModel(AigcModelEntity model);

    ChatModel buildChatModel(AigcModelEntity model);

    EmbeddingModel buildEmbeddingModel(AigcModelEntity model);

    ImageModel buildImageModel(AigcModelEntity model);

}
