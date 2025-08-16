package com.shuyoutech.aigc.provider.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.aigc.domain.model.ChatMessage;
import com.shuyoutech.aigc.domain.model.ChatModelBuilder;
import com.shuyoutech.aigc.domain.model.UserModelUsage;
import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import com.shuyoutech.aigc.listener.SSEChatEventListener;
import com.shuyoutech.aigc.provider.service.ModelService;
import com.shuyoutech.common.core.util.BooleanUtils;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapUtils;
import com.shuyoutech.common.core.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.shuyoutech.aigc.constant.AiConstants.*;
import static com.shuyoutech.aigc.provider.AigcModelFactory.MEDIA_TYPE_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * <a href="https://docs.anthropic.com/en/api/messages">role:developer,user,assistant,tool</a>
 *
 * @author YangChao
 * @date 2025-07-13 20:18
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AnthropicModelService implements ModelService {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.ANTHROPIC.getValue();
    }

    @Override
    public void chat(ChatModelBuilder builder, HttpServletResponse response) {
        try {
            UserModelUsage userToken = builder.getUserToken();
            JSONObject modelParam = builder.getModelParam();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // claude
            paramMap.put("model", builder.getModelName());
            String prompt = modelParam.getString("prompt");
            if (StringUtils.isNotBlank(prompt)) {
                JSONObject system = new JSONObject();
                system.put("type", "text");
                system.put("text", prompt);
                paramMap.put("system", List.of(system));
            }
            // role: developer、system、user、assistant、tool
            List<ChatMessage> messages = CollectionUtils.newArrayList();
            if (CollectionUtils.isNotEmpty(userToken.getMessages())) {
                messages.addAll(userToken.getMessages());
            }
            messages.add(ChatMessage.builder().role(ROLE_USER).content(modelParam.getString("message")).build());
            paramMap.put("messages", messages);
            paramMap.put("stream", true);
            paramMap.put("max_tokens", modelParam.getIntValue("max_tokens", 1024));
            paramMap.put("container", modelParam.get("container"));
            paramMap.put("mcp_servers", modelParam.get("mcp_servers"));
            paramMap.put("metadata", modelParam.get("metadata"));
            paramMap.put("service_tier", modelParam.get("service_tier"));
            paramMap.put("stop_sequences", modelParam.get("stop_sequences"));
            paramMap.put("temperature", modelParam.get("temperature"));
            if (BooleanUtils.isTrue(modelParam.getBoolean("enable_thinking"))) {
                JSONObject thinking = new JSONObject();
                thinking.put("type", "enabled");
                thinking.put("budget_tokens", modelParam.getIntValue("budget_tokens", 2000));
                paramMap.put("thinking", thinking);
            }
            paramMap.put("tool_choice", modelParam.get("tool_choice"));
            paramMap.put("tools", modelParam.get("tools"));
            paramMap.put("top_k", modelParam.get("top_k"));
            paramMap.put("top_p", modelParam.get("top_p"));
            String requestBody = JSONObject.toJSONString(paramMap);
            log.info("chatCompletions ============================ Anthropic request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ANTHROPIC) + ANTHROPIC_CHAT_COMPLETIONS)//
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader("anthropic-version", "2023-06-01") //
                    .addHeader("x-api-key", StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ANTHROPIC)) //
                    .build();

            userToken.setRequestBody(requestBody);
            userToken.setEnableMemory(modelParam.getBooleanValue("enable_memory", false));
            userToken.setEnableThinking(modelParam.getBooleanValue("enable_thinking", false));
            userToken.setEnableSearch(modelParam.getBooleanValue("enable_search", false));
            SSEChatEventListener sseChatEventListener = new SSEChatEventListener(userToken, response);
            EventSource.Factory factory = EventSources.createFactory(client);
            factory.newEventSource(request, sseChatEventListener);
            boolean await = sseChatEventListener.getCountDownLatch().await(5, TimeUnit.MINUTES);
            if (!await) {
                log.error("chat Anthropic ============================ getCountDownLatch timed out");
            }
        } catch (Exception e) {
            log.error("chat Anthropic ===================== exception:{}", e.getMessage());
        }
    }

}
