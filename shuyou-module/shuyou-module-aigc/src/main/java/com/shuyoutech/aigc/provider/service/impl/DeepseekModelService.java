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
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.shuyoutech.aigc.constant.AiConstants.*;
import static com.shuyoutech.aigc.provider.AigcModelFactory.MEDIA_TYPE_JSON;
import static com.shuyoutech.common.core.constant.CommonConstants.HEADER_AUTHORIZATION_PREFIX;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * <a href="https://api-docs.deepseek.com/zh-cn/">role:system,user,assistant,tool</a>
 *
 * @author YangChao
 * @date 2025-07-13 20:18
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class DeepseekModelService implements ModelService {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.DEEPSEEK.getValue();
    }

    @Override
    public void chat(ChatModelBuilder builder, HttpServletResponse response) {
        String modelFunction = builder.getUserToken().getModelFunction();
        if (StringUtils.containsIgnoreCase(modelFunction, "betaCompletions")) {
            betaCompletions(builder, response);
        } else {
            chatCompletions(builder, response);
        }
    }

    private void chatCompletions(ChatModelBuilder builder, HttpServletResponse response) {
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
            // 介于 -2.0 和 2.0 之间的数字。如果该值为正，那么新 token 会根据其在已有文本中的出现频率受到相应的惩罚，降低模型重复相同内容的可能性。
            paramMap.put("frequency_penalty", modelParam.getDouble("frequency_penalty"));
            // 介于 1 到 8192 间的整数，限制一次请求中模型生成 completion 的最大 token 数。输入 token 和输出 token 的总长度受模型的上下文长度的限制。 如未指定 max_tokens参数，默认使用 4096。
            paramMap.put("max_tokens", modelParam.getInteger("max_tokens"));
            // 介于 -2.0 和 2.0 之间的数字。如果该值为正，那么新 token 会根据其是否已在已有文本中出现受到相应的惩罚，从而增加模型谈论新主题的可能性。
            paramMap.put("presence_penalty", modelParam.getDouble("presence_penalty"));
            // 一个 object，指定模型必须输出的格式。 设置为 { "type": "json_object" } 以启用 JSON 模式，该模式保证模型生成的消息是有效的 JSON。[text, json_object]
            paramMap.put("response_format", modelParam.get("response_format"));
            // 一个 string 或最多包含 16 个 string 的 list，在遇到这些词时，API 将停止生成更多的 token。
            paramMap.put("stop", modelParam.get("stop"));
            // 采样温度，介于 0 和 2 之间。更高的值，如 0.8，会使输出更随机，而更低的值，如 0.2，会使其更加集中和确定。 我们通常建议可以更改这个值或者更改 top_p，但不建议同时对两者进行修改。
            paramMap.put("temperature", modelParam.getDouble("temperature"));
            // 作为调节采样温度的替代方案，模型会考虑前 top_p 概率的 token 的结果。所以 0.1 就意味着只有包括在最高 10% 概率中的 token 会被考虑。 我们通常建议修改这个值或者更改 temperature，但不建议同时对两者进行修改。
            paramMap.put("top_p", modelParam.getDouble("top_p"));
            // 模型可能会调用的 tool 的列表。目前，仅支持 function 作为工具。使用此参数来提供以 JSON 作为输入参数的 function 列表。最多支持 128 个 function。
            paramMap.put("tools", modelParam.get("tools"));
            // 控制模型调用 tool 的行为。
            paramMap.put("tool_choice", modelParam.get("tool_choice"));
            // 是否返回所输出 token 的对数概率。如果为 true，则在 message 的 content 中返回每个输出 token 的对数概率。
            paramMap.put("logprobs", modelParam.getBoolean("logprobs"));
            // 一个介于 0 到 20 之间的整数 N，指定每个输出位置返回输出概率 top N 的 token，且返回这些 token 的对数概率。指定此参数时，logprobs 必须为 true。
            paramMap.put("top_logprobs", modelParam.getInteger("top_logprobs"));
            String requestBody = JSONObject.toJSONString(paramMap);
            log.info("chatCompletions ============================ Deepseek request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_DEEPSEEK) + DEEPSEEK_CHAT_COMPLETIONS)//
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_DEEPSEEK)) //
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
                log.error("chatCompletions Deepseek ============================ getCountDownLatch timed out");
            }
        } catch (Exception e) {
            log.error("chatCompletions Deepseek ===================== exception:{}", e.getMessage());
        }
    }

    private void betaCompletions(ChatModelBuilder builder, HttpServletResponse response) {
        try {
            UserModelUsage userToken = builder.getUserToken();
            JSONObject modelParam = builder.getModelParam();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 模型的 ID
            paramMap.put("model", builder.getModelName());
            // 用于生成完成内容的提示
            paramMap.put("prompt", modelParam.getString("prompt"));
            // 在输出中，把 prompt 的内容也输出出来
            paramMap.put("echo", modelParam.getBoolean("echo"));
            // 介于 -2.0 和 2.0 之间的数字。如果该值为正，那么新 token 会根据其在已有文本中的出现频率受到相应的惩罚，降低模型重复相同内容的可能性。
            paramMap.put("frequency_penalty", modelParam.getDouble("frequency_penalty"));
            // 制定输出中包含 logprobs 最可能输出 token 的对数概率，包含采样的 token。例如，如果 logprobs 是 20，API 将返回一个包含 20 个最可能的 token 的列表。API 将始终返回采样 token 的对数概率，因此响应中可能会有最多 logprobs+1 个元素。 logprobs 的最大值是 20。
            paramMap.put("logprobs", modelParam.getInteger("logprobs"));
            // 大生成 token 数量。
            paramMap.put("max_tokens", modelParam.getInteger("max_tokens"));
            // 介于 -2.0 和 2.0 之间的数字。如果该值为正，那么新 token 会根据其是否已在已有文本中出现受到相应的惩罚，从而增加模型谈论新主题的可能性。
            paramMap.put("presence_penalty", modelParam.get("presence_penalty"));
            // 一个 string 或最多包含 16 个 string 的 list，在遇到这些词时，API 将停止生成更多的 token。
            paramMap.put("stop", modelParam.get("stop"));
            // 如果设置为 True，将会以 SSE（server-sent events）的形式以流式发送消息增量。消息流以 data: [DONE] 结尾。
            paramMap.put("stream", true);
            // 当启用流式输出时，可通过将本参数设置为{"include_usage": true}，在输出的最后一行显示所使用的Token数。
            // 如果设置为false，则最后一行不显示使用的Token数。
            JSONObject options = new JSONObject();
            options.put("include_usage", true);
            paramMap.put("stream_options", options);
            // 制定被补全内容的后缀。
            paramMap.put("suffix", modelParam.getString("suffix"));
            // 采样温度，介于 0 和 2 之间。更高的值，如 0.8，会使输出更随机，而更低的值，如 0.2，会使其更加集中和确定。 我们通常建议可以更改这个值或者更改 top_p，但不建议同时对两者进行修改。
            paramMap.put("temperature", modelParam.getDouble("temperature"));
            // 作为调节采样温度的替代方案，模型会考虑前 top_p 概率的 token 的结果。所以 0.1 就意味着只有包括在最高 10% 概率中的 token 会被考虑。 我们通常建议修改这个值或者更改 temperature，但不建议同时对两者进行修改。
            paramMap.put("top_p", modelParam.get("top_p"));
            String requestBody = JSONObject.toJSONString(paramMap);
            log.info("betaCompletions Deepseek ============================ request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_DEEPSEEK) + DEEPSEEK_BETA_COMPLETIONS)//
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_DEEPSEEK)) //
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
                log.error("betaCompletions Deepseek ============================ getCountDownLatch timed out");
            }
        } catch (Exception e) {
            log.error("betaCompletions Deepseek ===================== exception:{}", e.getMessage());
        }
    }

}
