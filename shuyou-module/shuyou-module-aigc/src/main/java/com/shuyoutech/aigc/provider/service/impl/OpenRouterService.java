package com.shuyoutech.aigc.provider.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.aigc.domain.model.ChatMessage;
import com.shuyoutech.aigc.domain.model.ChatModelBuilder;
import com.shuyoutech.aigc.domain.model.CommonModelBuilder;
import com.shuyoutech.aigc.domain.model.UserModelUsage;
import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import com.shuyoutech.aigc.listener.SSEChatEventListener;
import com.shuyoutech.aigc.provider.service.ModelService;
import com.shuyoutech.api.model.RemoteSysFile;
import com.shuyoutech.api.service.RemoteSystemService;
import com.shuyoutech.common.core.model.R;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapUtils;
import com.shuyoutech.common.core.util.NumberUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.disruptor.model.DisruptorData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.shuyoutech.aigc.constant.AiConstants.*;
import static com.shuyoutech.aigc.enums.AiModelFunctionEnum.*;
import static com.shuyoutech.aigc.provider.AigcModelFactory.MEDIA_TYPE_JSON;
import static com.shuyoutech.common.core.constant.CommonConstants.HEADER_AUTHORIZATION_PREFIX;
import static com.shuyoutech.common.disruptor.init.DisruptorRunner.disruptorProducer;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author YangChao
 * @date 2025-07-13 20:18
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenRouterService implements ModelService {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.OPEN_ROUTER.getValue();
    }

    /**
     * <a href="https://openrouter.ai/models"></a>
     */
    @Override
    public void chat(ChatModelBuilder builder, HttpServletResponse response) {
        try {
            UserModelUsage userToken = builder.getUserToken();
            JSONObject modelParam = builder.getModelParam();
            String modelName = builder.getProvider() + "/" + builder.getModelName();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            paramMap.put("model", modelName.toLowerCase());
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
            paramMap.put("audio", modelParam.get("audio"));
            paramMap.put("frequency_penalty", modelParam.get("frequency_penalty"));
            paramMap.put("function_call", modelParam.get("function_call"));
            paramMap.put("functions", modelParam.get("functions"));
            paramMap.put("logit_bias", modelParam.get("logit_bias"));
            paramMap.put("logprobs", modelParam.get("logprobs"));
            paramMap.put("max_completion_tokens", modelParam.get("max_completion_tokens"));
            paramMap.put("max_tokens", modelParam.get("max_tokens"));
            paramMap.put("metadata", modelParam.get("metadata"));
            paramMap.put("modalities", modelParam.get("modalities"));
            paramMap.put("n", modelParam.get("n"));
            paramMap.put("parallel_tool_calls", modelParam.get("parallel_tool_calls"));
            paramMap.put("prediction", modelParam.get("prediction"));
            paramMap.put("presence_penalty", modelParam.get("presence_penalty"));
            paramMap.put("prompt_cache_key", modelParam.get("prompt_cache_key"));
            paramMap.put("reasoning_effort", modelParam.get("reasoning_effort"));
            paramMap.put("response_format", modelParam.get("response_format"));
            paramMap.put("safety_identifier", modelParam.get("safety_identifier"));
            paramMap.put("seed", modelParam.get("seed"));
            paramMap.put("service_tier", modelParam.get("service_tier"));
            paramMap.put("stop", modelParam.get("stop"));
            paramMap.put("store", modelParam.get("store"));
            paramMap.put("temperature", modelParam.get("temperature"));
            paramMap.put("tool_choice", modelParam.get("tool_choice"));
            paramMap.put("tools", modelParam.get("tools"));
            paramMap.put("top_logprobs", modelParam.get("top_logprobs"));
            paramMap.put("top_p", modelParam.get("top_p"));
            paramMap.put("web_search_options", modelParam.get("web_search_options"));
            String requestBody = JSONObject.toJSONString(paramMap);
            log.info("chat ============================ OpenRouter request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_OPEN_ROUTER) + OPENAI_CHAT_COMPLETIONS)//
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_OPEN_ROUTER)) //
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
                log.error("chat OpenRouter ============================ getCountDownLatch timed out");
            }
        } catch (Exception e) {
            log.error("chat OpenRouter ===================== exception:{}", e.getMessage());
        }
    }

    @Override
    public void image(CommonModelBuilder builder, HttpServletResponse response) {
        String modelFunction = builder.getUserModelUsage().getModelFunction();
        if (StringUtils.containsIgnoreCase(modelFunction, TEXT_TO_IMAGE.getValue())) {
            // 图像生成
            imagesGenerations(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, IMAGE_EDIT.getValue())) {
            // 图像编辑
            imagesEdits(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, IMAGE_VARIATION.getValue())) {
            // 图像变体-图片转换成其他像素
            imagesVariations(builder, response);
        }
    }

    /**
     * <a href="https://platform.openai.com/docs/api-reference/images/create">...</a>
     */
    private void imagesGenerations(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            paramMap.put("model", builder.getModelName());
            paramMap.put("prompt", modelParam.getString("prompt"));
            // transparent（透明）、opaque（不透明）或auto（自动，默认值）之一
            paramMap.put("background", StringUtils.blankToDefault(modelParam.getString("background"), "auto"));
            // low or auto
            paramMap.put("moderation", StringUtils.blankToDefault(modelParam.getString("moderation"), "auto"));
            paramMap.put("n", modelParam.getIntValue("n", 1));
            paramMap.put("output_compression", modelParam.getIntValue("output_compression", 100));
            // png, jpeg, or webp
            paramMap.put("output_format", StringUtils.blankToDefault(modelParam.getString("output_format"), "png"));
            paramMap.put("partial_images", modelParam.getIntValue("partial_images", 0));
            // auto (default value) will automatically select the best quality for the given model.
            // high, medium and low are supported for gpt-image-1.
            // hd and standard are supported for dall-e-3.
            // standard is the only option for dall-e-2.
            paramMap.put("quality", StringUtils.blankToDefault(modelParam.getString("quality"), "auto"));
            // url or b64_json
            paramMap.put("response_format", StringUtils.blankToDefault(modelParam.getString("response_format"), "url"));
            // 1024x1024, 1536x1024 (landscape), 1024x1536 (portrait), or auto (default value)
            paramMap.put("size", StringUtils.blankToDefault(modelParam.getString("size"), "auto"));
            paramMap.put("stream", modelParam.getBooleanValue("stream", false));
            // vivid or natural
            paramMap.put("style", StringUtils.blankToDefault(modelParam.getString("style"), "style"));
            paramMap.put("user", modelParam.getString("user"));
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("imagesGenerations ============================ OpenAI request:{}", requestBody);

            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_OPENAI) + OPENAI_IMAGES_GENERATIONS) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_OPEN_ROUTER)) //
                    .build();

            Response res = client.newCall(request).execute();
            dealImageResponse(userToken, res, response);
        } catch (Exception e) {
            log.error("imagesGenerations OpenAI ===================== exception:{}", e.getMessage());
        }
    }

    /**
     * <a href="https://platform.openai.com/docs/api-reference/images/createEdit">...</a>
     */
    private void imagesEdits(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            UserModelUsage userToken = builder.getUserModelUsage();
            JSONObject modelParam = builder.getModelParam();
            JSONArray imageIds = modelParam.getJSONArray("imageIds");
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            bodyBuilder.addFormDataPart("model", builder.getModelName());
            File image;
            for (int i = 0; i < imageIds.size(); i++) {
                image = new File(remoteSystemService.getFilePath(imageIds.getString(i)));
                bodyBuilder.addFormDataPart("image", image.getName(), RequestBody.create(image, MediaType.get(FileUtil.getMimeType(image.getName()))));
            }
            bodyBuilder.addFormDataPart("prompt", modelParam.getString("prompt"));
            bodyBuilder.addFormDataPart("background", modelParam.getString("background"));
            bodyBuilder.addFormDataPart("n", StringUtils.blankToDefault(modelParam.getString("n"), "1"));
            bodyBuilder.addFormDataPart("output_compression", modelParam.getString("output_compression"));
            bodyBuilder.addFormDataPart("output_format", StringUtils.blankToDefault(modelParam.getString("output_format"), "png"));
            bodyBuilder.addFormDataPart("quality", StringUtils.blankToDefault(modelParam.getString("quality"), "auto"));
            bodyBuilder.addFormDataPart("response_format", StringUtils.blankToDefault(modelParam.getString("response_format"), "url"));
            bodyBuilder.addFormDataPart("size", StringUtils.blankToDefault(modelParam.getString("size"), "1024x1024"));
            bodyBuilder.addFormDataPart("user", modelParam.getString("user"));
            String requestBody = JSON.toJSONString(builder);
            userToken.setRequestBody(requestBody);
            log.info("imagesEdits ============================ OpenAI request:{}", requestBody);
            MultipartBody body = bodyBuilder.build();

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_OPENAI) + IMAGES_EDITS) //
                    .post(body) //
                    .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_OPEN_ROUTER)) //
                    .build();

            Response res = client.newCall(request).execute();
            dealImageResponse(userToken, res, response);
        } catch (Exception e) {
            log.error("imagesEdits OpenAI ===================== exception:{}", e.getMessage());
        }
    }

    /**
     * <a href="https://platform.openai.com/docs/api-reference/images/createVariation">...</a>
     */
    private void imagesVariations(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            String imageId = modelParam.getString("imageId");
            File image = new File(remoteSystemService.getFilePath(imageId));
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            bodyBuilder.addFormDataPart("model", builder.getModelName());
            bodyBuilder.addFormDataPart("image", image.getName(), RequestBody.create(image, MediaType.get(FileUtil.getMimeType(image.getName()))));
            bodyBuilder.addFormDataPart("prompt", modelParam.getString("prompt"));
            bodyBuilder.addFormDataPart("n", StringUtils.blankToDefault(modelParam.getString("n"), "1"));
            bodyBuilder.addFormDataPart("response_format", StringUtils.blankToDefault(modelParam.getString("response_format"), "url"));
            bodyBuilder.addFormDataPart("size", StringUtils.blankToDefault(modelParam.getString("size"), "1024x1024"));
            bodyBuilder.addFormDataPart("user", modelParam.getString("user"));
            String requestBody = JSON.toJSONString(builder);
            userToken.setRequestBody(requestBody);
            log.info("imagesVariations ============================ OpenAI request:{}", requestBody);
            MultipartBody body = bodyBuilder.build();

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_OPENAI) + OPENAI_IMAGES_VARIATIONS) //
                    .post(body) //
                    .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_OPEN_ROUTER)) //
                    .build();

            Response res = client.newCall(request).execute();
            dealImageResponse(userToken, res, response);
        } catch (Exception e) {
            log.error("imagesVariations OpenAI ===================== exception:{}", e.getMessage());
        }
    }

    private void dealImageResponse(UserModelUsage userToken, Response res, HttpServletResponse response) {
        try {
            if (res.isSuccessful()) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("dealImageResponse OpenAI ====================== data:{}", bodyStr);
                JSONObject bodyObject = JSONObject.parseObject(bodyStr);
                List<String> list = CollectionUtils.newArrayList();
                JSONArray results = bodyObject.getJSONArray("data");
                for (int j = 0; j < results.size(); j++) {
                    JSONObject dataObject = results.getJSONObject(j);
                    if (dataObject.containsKey("url")) {
                        list.add(dataObject.getString("url"));
                    } else {
                        list.add(dataObject.getString("b64_json"));
                    }
                }
                Date end = new Date();
                long costTime = end.getTime() - userToken.getRequestTime().getTime();
                userToken.setResponseTime(end);
                userToken.setDurationSeconds(NumberUtils.div(String.valueOf(costTime), "1000", 2));
                userToken.setInputTokenCount(bodyObject.getJSONObject("usage").getIntValue("input_tokens", 0));
                userToken.setOutputTokenCount(bodyObject.getJSONObject("usage").getIntValue("output_tokens", 0));
                userToken.setTotalTokenCount(bodyObject.getJSONObject("usage").getIntValue("total_tokens", 0));
                userToken.setTotalCount(list.size());
                userToken.setResponseBody(bodyObject.toJSONString());
                DisruptorData disruptorData = new DisruptorData();
                disruptorData.setServiceName(USER_IMAGE_SERVICE);
                disruptorData.setData(userToken);
                disruptorProducer.pushData(disruptorData);

                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(R.success(list)));
                writer.flush();
            } else {
                response.setStatus(res.code());
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(R.error(new String(res.body().bytes(), StandardCharsets.UTF_8))));
                writer.flush();
                log.error("dealImageResponse OpenAI ================= code:{},response:{}", res.code(), res.message());
            }
        } catch (Exception e) {
            log.error("dealImageResponse OpenAI ===================== exception:{}", e.getMessage());
        }
    }

    @Override
    public void audio(CommonModelBuilder builder, HttpServletResponse response) {
        String modelFunction = builder.getUserModelUsage().getModelFunction();
        if (StringUtils.containsIgnoreCase(modelFunction, AUDIO_SPEECH.getValue())) {
            // 文本转音频
            audioSpeech(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, AUDIO_TRANSCRIPTION.getValue())) {
            // 音频转文本
            audioTranscriptions(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, AUDIO_TRANSLATION.getValue())) {
            // 音频翻译
            audioTranslations(builder, response);
        }
    }

    private void audioSpeech(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            String modelName = builder.getModelName();
            UserModelUsage modelUsage = builder.getUserModelUsage();
            JSONObject modelParam = builder.getModelParam();
            String responseFormat = modelParam.getString("response_format");
            Map<String, Object> paramMap = MapUtils.newHashMap();
            paramMap.put("model", modelName);
            paramMap.put("input", modelParam.getString("input"));
            paramMap.put("voice", StringUtils.blankToDefault(modelParam.getString("voice"), "alloy"));
            paramMap.put("instructions", modelParam.getString("instructions"));
            paramMap.put("response_format", responseFormat);
            paramMap.put("speed", modelParam.getDouble("speed"));
            paramMap.put("stream_format", modelParam.getString("stream_format"));
            String requestBody = JSONObject.toJSONString(paramMap);
            modelUsage.setRequestBody(requestBody);
            log.info("audioSpeech OpenAI ============================  request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_OPENAI) + OPENAI_AUDIO_SPEECH) //
                    .post(body) //
                    .addHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(org.springframework.http.HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_OPEN_ROUTER)) //
                    .build();

            Response res = client.newCall(request).execute();
            if (res.isSuccessful()) {
                RemoteSysFile fileVo = remoteSystemService.upload(IdUtil.fastSimpleUUID() + "." + responseFormat, res.body().bytes());

                Date end = new Date();
                long costTime = end.getTime() - modelUsage.getRequestTime().getTime();
                modelUsage.setResponseTime(end);
                modelUsage.setDurationSeconds(NumberUtils.div(String.valueOf(costTime), "1000", 2));
                modelUsage.setTotalCount(1);
                modelUsage.setResponseBody(new String(res.body().bytes(), StandardCharsets.UTF_8));
                DisruptorData disruptorData = new DisruptorData();
                disruptorData.setServiceName(USER_IMAGE_SERVICE);
                disruptorData.setData(modelUsage);
                disruptorProducer.pushData(disruptorData);

                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(R.success(JSON.toJSONString(fileVo))));
                writer.flush();
            } else {
                response.setStatus(res.code());
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(R.error(new String(res.body().bytes(), StandardCharsets.UTF_8))));
                writer.flush();
                log.error("audioSpeech OpenAI ================= code:{},response:{}", res.code(), res.message());
            }
        } catch (Exception e) {
            log.error("audioSpeech OpenAI ===================== exception:{}", e.getMessage());
        }
    }

    private void audioTranscriptions(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            String fileId = modelParam.getString("fileId");
            File file = new File(remoteSystemService.getFilePath(fileId));
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            bodyBuilder.addFormDataPart("model", builder.getModelName());
            bodyBuilder.addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.get(FileUtil.getMimeType(file.getName()))));
            if (StringUtils.isNotBlank(modelParam.getString("language"))) {
                bodyBuilder.addFormDataPart("language", modelParam.getString("language"));
            }
            if (StringUtils.isNotBlank(modelParam.getString("prompt"))) {
                bodyBuilder.addFormDataPart("prompt", modelParam.getString("prompt"));
            }
            if (StringUtils.isNotBlank(modelParam.getString("response_format"))) {
                bodyBuilder.addFormDataPart("response_format", modelParam.getString("response_format"));
            }
            String requestBody = JSON.toJSONString(builder);
            userToken.setRequestBody(requestBody);
            log.info("audioTranscriptions ============================ OpenAI request:{}", requestBody);
            MultipartBody body = bodyBuilder.build();

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_OPENAI) + OPENAI_AUDIO_TRANSCRIPTIONS) //
                    .post(body) //
                    .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_OPEN_ROUTER)) //
                    .build();

            Response res = client.newCall(request).execute();
            if (res.isSuccessful()) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("audioTranscriptions OpenAI ====================== data:{}", bodyStr);
                JSONObject bodyObject = JSONObject.parseObject(bodyStr);

                Date end = new Date();
                long costTime = end.getTime() - userToken.getRequestTime().getTime();
                userToken.setResponseTime(end);
                userToken.setDurationSeconds(NumberUtils.div(String.valueOf(costTime), "1000", 2));
                userToken.setInputTokenCount(bodyObject.getJSONObject("usage").getIntValue("input_tokens", 0));
                userToken.setOutputTokenCount(bodyObject.getJSONObject("usage").getIntValue("output_tokens", 0));
                userToken.setTotalTokenCount(bodyObject.getJSONObject("usage").getIntValue("total_tokens", 0));
                userToken.setResponseBody(bodyObject.toJSONString());
                DisruptorData disruptorData = new DisruptorData();
                disruptorData.setServiceName(USER_IMAGE_SERVICE);
                disruptorData.setData(userToken);
                disruptorProducer.pushData(disruptorData);

                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(R.success(bodyObject.getString("text"))));
                writer.flush();
            } else {
                response.setStatus(res.code());
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(R.error(new String(res.body().bytes(), StandardCharsets.UTF_8))));
                writer.flush();
                log.error("audioTranscriptions OpenAI ================= code:{},response:{}", res.code(), res.message());
            }
        } catch (Exception e) {
            log.error("audioTranscriptions OpenAI ===================== exception:{}", e.getMessage());
        }
    }

    private void audioTranslations(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            String fileId = modelParam.getString("fileId");
            File file = new File(remoteSystemService.getFilePath(fileId));
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            bodyBuilder.addFormDataPart("model", builder.getModelName());
            bodyBuilder.addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.get(FileUtil.getMimeType(file.getName()))));
            if (StringUtils.isNotBlank(modelParam.getString("prompt"))) {
                bodyBuilder.addFormDataPart("prompt", modelParam.getString("prompt"));
            }
            if (StringUtils.isNotBlank(modelParam.getString("response_format"))) {
                bodyBuilder.addFormDataPart("response_format", modelParam.getString("response_format"));
            }
            if (StringUtils.isNotBlank(modelParam.getString("temperature"))) {
                bodyBuilder.addFormDataPart("temperature", modelParam.getString("temperature"));
            }
            String requestBody = JSON.toJSONString(builder);
            userToken.setRequestBody(requestBody);
            log.info("audioTranslations ============================ OpenAI request:{}", requestBody);
            MultipartBody body = bodyBuilder.build();

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_OPENAI) + OPENAI_AUDIO_TRANSCRIPTIONS) //
                    .post(body) //
                    .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_OPEN_ROUTER)) //
                    .build();

            Response res = client.newCall(request).execute();
            if (res.isSuccessful()) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("audioTranslations OpenAI ====================== data:{}", bodyStr);
                JSONObject bodyObject = JSONObject.parseObject(bodyStr);

                Date end = new Date();
                long costTime = end.getTime() - userToken.getRequestTime().getTime();
                userToken.setResponseTime(end);
                userToken.setDurationSeconds(NumberUtils.div(String.valueOf(costTime), "1000", 2));
                userToken.setInputTokenCount(bodyObject.getJSONObject("usage").getIntValue("input_tokens", 0));
                userToken.setOutputTokenCount(bodyObject.getJSONObject("usage").getIntValue("output_tokens", 0));
                userToken.setTotalTokenCount(bodyObject.getJSONObject("usage").getIntValue("total_tokens", 0));
                userToken.setResponseBody(bodyObject.toJSONString());
                DisruptorData disruptorData = new DisruptorData();
                disruptorData.setServiceName(USER_IMAGE_SERVICE);
                disruptorData.setData(userToken);
                disruptorProducer.pushData(disruptorData);

                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(R.success(bodyObject.getString("text"))));
                writer.flush();
            } else {
                response.setStatus(res.code());
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(R.error(new String(res.body().bytes(), StandardCharsets.UTF_8))));
                writer.flush();
                log.error("audioTranslations OpenAI ================= code:{},response:{}", res.code(), res.message());
            }
        } catch (Exception e) {
            log.error("audioTranslations OpenAI ===================== exception:{}", e.getMessage());
        }
    }

    private final RemoteSystemService remoteSystemService;

}
