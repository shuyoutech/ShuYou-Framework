package com.shuyoutech.aigc.provider.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.aigc.domain.model.ChatMessage;
import com.shuyoutech.aigc.domain.model.ChatModelBuilder;
import com.shuyoutech.aigc.domain.model.UserModelUsage;
import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import com.shuyoutech.aigc.listener.SSEChatEventListener;
import com.shuyoutech.aigc.provider.service.ModelService;
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
import static com.shuyoutech.common.core.constant.CommonConstants.HEADER_AUTHORIZATION_PREFIX;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * <a href="https://docs.x.ai/docs/api-reference">role:developer,user,assistant,tool</a>
 *
 * @author YangChao
 * @date 2025-07-13 20:18
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class XAIModelService implements ModelService {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.XAI.getValue();
    }

    @Override
    public void chat(ChatModelBuilder builder, HttpServletResponse response) {
        try {
            UserModelUsage userToken = builder.getUserToken();
            JSONObject modelParam = builder.getModelParam();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            paramMap.put("model", builder.getModelName());
            // role: system、user、assistant、tool
            List<ChatMessage> messages = CollectionUtils.newArrayList();
            String prompt = modelParam.getString("prompt");
            if (StringUtils.isNotBlank(prompt)) {
                messages.add(ChatMessage.builder().role(ROLE_SYSTEM).content(prompt).build());
            }
            if (CollectionUtils.isNotEmpty(userToken.getMessages())) {
                messages.addAll(userToken.getMessages());
            }
            messages.add(ChatMessage.builder().role(ROLE_USER).content(modelParam.getString("message")).build());
            paramMap.put("messages", messages);
            paramMap.put("stream", true);
            // 当启用流式输出时，可通过将本参数设置为{"include_usage": true}，在输出的最后一行显示所使用的Token数。
            // 如果设置为false，则最后一行不显示使用的Token数。
            JSONObject options = new JSONObject();
            options.put("include_usage", true);
            paramMap.put("stream_options", options);
            paramMap.put("deferred", modelParam.get("deferred"));
            paramMap.put("frequency_penalty", modelParam.get("frequency_penalty"));
            paramMap.put("logit_bias", modelParam.get("logit_bias"));
            paramMap.put("logprobs", modelParam.get("logprobs"));
            paramMap.put("max_completion_tokens", modelParam.get("max_completion_tokens"));
            paramMap.put("max_tokens", modelParam.get("max_tokens"));
            paramMap.put("n", modelParam.get("n"));
            paramMap.put("parallel_tool_calls", modelParam.get("parallel_tool_calls"));
            paramMap.put("presence_penalty", modelParam.get("presence_penalty"));
            paramMap.put("reasoning_effort", modelParam.get("reasoning_effort"));
            paramMap.put("response_format", modelParam.get("response_format"));
            paramMap.put("search_parameters", modelParam.get("search_parameters"));
            paramMap.put("seed", modelParam.get("seed"));
            paramMap.put("stop", modelParam.get("stop"));
            paramMap.put("temperature", modelParam.get("temperature"));
            paramMap.put("tool_choice", modelParam.get("tool_choice"));
            paramMap.put("tools", modelParam.get("tools"));
            paramMap.put("top_logprobs", modelParam.get("top_logprobs"));
            paramMap.put("top_p", modelParam.get("top_p"));
            paramMap.put("user", modelParam.get("user"));
            paramMap.put("web_search_options", modelParam.get("web_search_options"));
            String requestBody = JSONObject.toJSONString(paramMap);
            log.info("chat ============================ X-AI request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_XAI) + "/v1/chat/completions")//
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_XAI)) //
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
                log.error("chat X-AI ============================ getCountDownLatch timed out");
            }
        } catch (Exception e) {
            log.error("chat X-AI ===================== exception:{}", e.getMessage());
        }
    }

}
