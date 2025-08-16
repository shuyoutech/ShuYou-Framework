package com.shuyoutech.aigc.provider.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.aigc.domain.model.ChatModelBuilder;
import com.shuyoutech.aigc.domain.model.CommonModelBuilder;
import com.shuyoutech.aigc.domain.model.UserModelUsage;
import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import com.shuyoutech.aigc.listener.SSEChatEventListener;
import com.shuyoutech.aigc.provider.service.ModelService;
import com.shuyoutech.common.core.model.R;
import com.shuyoutech.common.core.util.*;
import com.shuyoutech.common.disruptor.model.DisruptorData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.shuyoutech.aigc.constant.AiConstants.*;
import static com.shuyoutech.aigc.provider.AigcModelFactory.MEDIA_TYPE_JSON;
import static com.shuyoutech.common.disruptor.init.DisruptorRunner.disruptorProducer;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * <a href="nhttps://ai.google.dev/gemini-api/docs?hl=zh-cn">Gemini</a>
 *
 * @author YangChao
 * @date 2025-07-13 20:18
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleModelService implements ModelService {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.GOOGLE.getValue();
    }

    @Override
    public void chat(ChatModelBuilder builder, HttpServletResponse response) {
        try {
            UserModelUsage userToken = builder.getUserToken();
            JSONObject modelParam = builder.getModelParam();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            String prompt = modelParam.getString("prompt");
            if (StringUtils.isNotBlank(prompt)) {
                JSONObject part = new JSONObject();
                part.put("text", prompt);
                JSONObject systemInstruction = new JSONObject();
                systemInstruction.put("parts", List.of(part));
                paramMap.put("system_instruction", systemInstruction);
            }
            List<JSONObject> contents = CollectionUtils.newArrayList();
            JSONObject part = new JSONObject();
            part.put("text", modelParam.getString("message"));
            JSONObject content = new JSONObject();
            content.put("parts", List.of(part));
            // 必须是“user”或“model”。
            content.put("role", "user");
            contents.add(content);
            paramMap.put("contents", contents);
            paramMap.put("tools", modelParam.get("tools"));
            paramMap.put("tool_config", modelParam.get("tool_config"));
            paramMap.put("safety_settings", modelParam.get("safety_settings"));
            JSONObject generationConfig = new JSONObject();
            generationConfig.put("stop_sequences", modelParam.get("stop_sequences"));
            generationConfig.put("response_mime_type", modelParam.get("response_mime_type"));
            generationConfig.put("response_schema", modelParam.get("response_schema"));
            generationConfig.put("response_json_schema", modelParam.get("response_json_schema"));
            generationConfig.put("response_modalities", modelParam.get("response_modalities"));
            generationConfig.put("candidate_count", modelParam.get("candidate_count"));
            generationConfig.put("max_output_tokens", modelParam.get("max_output_tokens"));
            generationConfig.put("temperature", modelParam.get("temperature"));
            generationConfig.put("top_p", modelParam.get("top_p"));
            generationConfig.put("top_k", modelParam.get("top_k"));
            generationConfig.put("seed", modelParam.get("seed"));
            generationConfig.put("presence_penalty", modelParam.get("presence_penalty"));
            generationConfig.put("frequency_penalty", modelParam.get("frequency_penalty"));
            generationConfig.put("response_logprobs", modelParam.get("response_logprobs"));
            generationConfig.put("logprobs", modelParam.get("logprobs"));
            generationConfig.put("enable_enhanced_civic_answers", modelParam.get("enable_enhanced_civic_answers"));
            // 语音生成配置
            generationConfig.put("speech_config", modelParam.get("speech_config"));
            if (BooleanUtils.isTrue(modelParam.getBoolean("enable_thinking"))) {
                JSONObject thinkingConfig = new JSONObject();
                thinkingConfig.put("include_thoughts", true);
                thinkingConfig.put("thinking_budget", modelParam.get("thinking_budget"));
                generationConfig.put("thinking_config", thinkingConfig);
            }
            // 输入媒体的媒体分辨率
            // MEDIA_RESOLUTION_UNSPECIFIED	尚未设置媒体分辨率。
            // MEDIA_RESOLUTION_LOW	媒体分辨率设置为低 (64 个令牌)。
            // MEDIA_RESOLUTION_MEDIUM	媒体分辨率设置为中等（256 个令牌）。
            // MEDIA_RESOLUTION_HIGH	媒体分辨率设置为高（使用 256 个令牌进行缩放重构）。
            generationConfig.put("media_resolution", modelParam.get("media_resolution"));
            paramMap.put("generation_config", generationConfig);
            paramMap.put("cached_content", modelParam.get("cached_content"));
            String requestBody = JSONObject.toJSONString(paramMap);
            log.info("chat ============================ Google request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_GOOGLE) + StringUtils.format("/v1beta/models/{}:streamGenerateContent?alt=sse", builder.getModelName()))//
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader("x-goog-api-key", StringUtils.blankToDefault(builder.getApiKey(), API_KEY_GOOGLE)) //
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
                log.error("chat Google ============================ getCountDownLatch timed out");
            }
        } catch (Exception e) {
            log.error("chat Google ===================== exception:{}", e.getMessage());
        }
    }

    @Override
    public void image(CommonModelBuilder builder, HttpServletResponse response) {
        generateContent(builder, response);
    }

    private void generateContent(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            String prompt = modelParam.getString("prompt");
            if (StringUtils.isNotBlank(prompt)) {
                JSONObject part = new JSONObject();
                part.put("text", prompt);
                JSONObject systemInstruction = new JSONObject();
                systemInstruction.put("parts", List.of(part));
                paramMap.put("system_instruction", systemInstruction);
            }
            List<JSONObject> contents = CollectionUtils.newArrayList();
            JSONObject part = new JSONObject();
            part.put("text", modelParam.getString("message"));
            JSONObject content = new JSONObject();
            content.put("parts", List.of(part));
            // 必须是“user”或“model”。
            content.put("role", "user");
            contents.add(content);
            paramMap.put("contents", contents);
            JSONObject generationConfig = new JSONObject();
            generationConfig.put("stop_sequences", modelParam.get("stop_sequences"));
            generationConfig.put("response_mime_type", modelParam.get("response_mime_type"));
            generationConfig.put("response_schema", modelParam.get("response_schema"));
            generationConfig.put("response_json_schema", modelParam.get("response_json_schema"));
            generationConfig.put("response_modalities", modelParam.get("response_modalities"));
            generationConfig.put("candidate_count", modelParam.get("candidate_count"));
            generationConfig.put("max_output_tokens", modelParam.get("max_output_tokens"));
            generationConfig.put("temperature", modelParam.get("temperature"));
            generationConfig.put("top_p", modelParam.get("top_p"));
            generationConfig.put("top_k", modelParam.get("top_k"));
            generationConfig.put("seed", modelParam.get("seed"));
            generationConfig.put("presence_penalty", modelParam.get("presence_penalty"));
            generationConfig.put("frequency_penalty", modelParam.get("frequency_penalty"));
            generationConfig.put("response_logprobs", modelParam.get("response_logprobs"));
            generationConfig.put("logprobs", modelParam.get("logprobs"));
            generationConfig.put("enable_enhanced_civic_answers", modelParam.get("enable_enhanced_civic_answers"));
            // 语音生成配置
            generationConfig.put("speech_config", modelParam.get("speech_config"));
            if (BooleanUtils.isTrue(modelParam.getBoolean("enable_thinking"))) {
                JSONObject thinkingConfig = new JSONObject();
                thinkingConfig.put("include_thoughts", true);
                thinkingConfig.put("thinking_budget", modelParam.get("thinking_budget"));
                generationConfig.put("thinking_config", thinkingConfig);
            }
            // 输入媒体的媒体分辨率
            // MEDIA_RESOLUTION_UNSPECIFIED	尚未设置媒体分辨率。
            // MEDIA_RESOLUTION_LOW	媒体分辨率设置为低 (64 个令牌)。
            // MEDIA_RESOLUTION_MEDIUM	媒体分辨率设置为中等（256 个令牌）。
            // MEDIA_RESOLUTION_HIGH	媒体分辨率设置为高（使用 256 个令牌进行缩放重构）。
            generationConfig.put("media_resolution", modelParam.get("media_resolution"));
            paramMap.put("generation_config", generationConfig);
            paramMap.put("cached_content", modelParam.get("cached_content"));
            String requestBody = JSONObject.toJSONString(paramMap);
            log.info("generateContent ============================ Google request:{}", requestBody);
            UserModelUsage userToken = builder.getUserModelUsage();
            userToken.setRequestBody(requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_GOOGLE) //
                            + StringUtils.format(GOOGLE_GENERATE_CONTENT, builder.getModelName(), StringUtils.blankToDefault(builder.getApiKey(), API_KEY_GOOGLE)))//
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader("x-goog-api-key", StringUtils.blankToDefault(builder.getApiKey(), API_KEY_GOOGLE)) //
                    .build();

            Response res = client.newCall(request).execute();
            dealResponse(userToken, res, response);
        } catch (Exception e) {
            log.error("generateContent Google ===================== exception:{}", e.getMessage());
        }
    }

    /**
     * 处理返回结果
     */
    private void dealResponse(UserModelUsage tokenUsage, Response res, HttpServletResponse response) {
        try {
            if (res.isSuccessful() ) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("dealResponse Google ====================== body:{}", bodyStr);
                JSONObject bodyObject = JSONObject.parseObject(bodyStr);
                JSONObject usage = bodyObject.getJSONObject("usageMetadata");
                if (null != usage) {
                    tokenUsage.setInputTokenCount(usage.getIntValue("promptTokenCount", 0));
                    tokenUsage.setOutputTokenCount(usage.getIntValue("candidatesTokenCount", 0));
                    tokenUsage.setTotalTokenCount(usage.getIntValue("totalTokenCount", 0));
                }
                String data = null;
                JSONArray candidates = bodyObject.getJSONArray("candidates");
                if (null != candidates && !candidates.isEmpty()) {
                    JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                    if (null != content && null != content.getJSONArray("parts") && !content.getJSONArray("parts").isEmpty()) {
                        JSONArray parts = content.getJSONArray("parts");
                        JSONObject part = parts.getJSONObject(0);
                        if (StringUtils.isNotBlank(part.getString("text"))) {
                            data = part.getString("text");
                        } else if (part.containsKey("inlineData") && null != part.getJSONObject("inlineData")) {
                            JSONObject jsonObject = part.getJSONObject("inlineData");
                            data = jsonObject.getString("data");
                        } else if (part.containsKey("fileData") && null != part.getJSONObject("fileData")) {
                            JSONObject jsonObject = part.getJSONObject("fileData");
                            data = jsonObject.getString("fileUri");
                        }
                    }
                }
                Date end = new Date();
                long costTime = end.getTime() - tokenUsage.getRequestTime().getTime();
                tokenUsage.setResponseTime(end);
                tokenUsage.setDurationSeconds(NumberUtils.div(String.valueOf(costTime), "1000", 2));
                tokenUsage.setResponseBody(bodyObject.toJSONString());
                DisruptorData disruptorData = new DisruptorData();
                disruptorData.setServiceName(USER_IMAGE_SERVICE);
                disruptorData.setData(tokenUsage);
                disruptorProducer.pushData(disruptorData);

                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(R.error(data)));
                writer.flush();
            } else {
                response.setStatus(res.code());
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(R.error(new String(res.body().bytes(), StandardCharsets.UTF_8))));
                writer.flush();
                log.error("dealResponse Google ================= code:{},response:{}", res.code(), res.message());
            }
        } catch (Exception e) {
            log.error("dealResponse Google ===================== exception:{}", e.getMessage());
        }
    }

}
