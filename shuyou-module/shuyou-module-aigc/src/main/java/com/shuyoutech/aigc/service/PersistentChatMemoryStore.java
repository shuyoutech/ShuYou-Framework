package com.shuyoutech.aigc.service;

import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.redis.constant.CacheConstants;
import com.shuyoutech.common.redis.util.RedisUtils;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

/**
 * @author YangChao
 * @date 2025-05-17 10:30
 **/
@Slf4j
public class PersistentChatMemoryStore implements ChatMemoryStore {

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String memoryJson = RedisUtils.getString(CacheConstants.AI_MEMORY_ID + memoryId.toString());
        if (StringUtils.isBlank(memoryJson)) {
            return new ArrayList<>();
        }
        return messagesFromJson(memoryJson);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String json = messagesToJson(messages);
        RedisUtils.set(CacheConstants.AI_MEMORY_ID + memoryId.toString(), json, 1L, TimeUnit.DAYS);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        RedisUtils.delete(CacheConstants.AI_MEMORY_ID + memoryId.toString());
    }

}
