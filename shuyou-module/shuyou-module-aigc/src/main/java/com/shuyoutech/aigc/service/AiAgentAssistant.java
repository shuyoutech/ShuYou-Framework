package com.shuyoutech.aigc.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

/**
 * @author YangChao
 * @date 2025-05-17 10:26
 **/
public interface AiAgentAssistant {

    TokenStream chat(@MemoryId String id, @UserMessage String message);

    String text(@MemoryId String id, @UserMessage String message);

}
