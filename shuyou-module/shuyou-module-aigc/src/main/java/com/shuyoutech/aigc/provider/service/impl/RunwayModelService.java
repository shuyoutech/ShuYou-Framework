package com.shuyoutech.aigc.provider.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.aigc.domain.model.CommonModelBuilder;
import com.shuyoutech.aigc.domain.model.UserModelUsage;
import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import com.shuyoutech.aigc.provider.service.ModelService;
import com.shuyoutech.common.core.model.R;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapUtils;
import com.shuyoutech.common.core.util.NumberUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.disruptor.model.DisruptorData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.stereotype.Component;

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
 * <a href="https://dev.runwayml.com/">专业创作首选，适合影视级精细编辑用户(如复杂场景、人物表情优化)、专业影视创作者(需高稳定性)</a>
 *
 * @author YangChao
 * @date 2025-07-29 10:26
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class RunwayModelService implements ModelService {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.RUNWAY.getValue();
    }

    /**
     * <a href="https://docs.dev.runwayml.com/api/#tag/Start-generating/paths/~1v1~1text_to_image/post">...</a>
     */
    @Override
    public void image(CommonModelBuilder builder, HttpServletResponse response) {
        String modelFunction = builder.getUserModelUsage().getModelFunction();
        if (StringUtils.containsIgnoreCase(modelFunction, TEXT_TO_IMAGE.getValue()) //
                || StringUtils.containsIgnoreCase(modelFunction, IMAGE_TO_IMAGE.getValue())) {
            textToImage(builder, response);
        }
    }

    private void textToImage(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            paramMap.put("model", builder.getModelName());
            paramMap.put("promptText", modelParam.getString("prompt"));
            // "1920:1080" "1080:1920" "1024:1024" "1360:768" "1080:1080" "1168:880" "1440:1080" "1080:1440" "1808:768" "2112:912" "1280:720" "720:1280" "720:720" "960:720" "720:960" "1680:720"
            paramMap.put("ratio", StringUtils.blankToDefault(modelParam.getString("ratio"), "1920:1080"));
            // [ 0 .. 4294967295 ]
            paramMap.put("seed", modelParam.getInteger("seed"));
            // array { "uri": "http://example.com",  "tag": "string" }
            paramMap.put("referenceImages", modelParam.get("referenceImages"));
            // Settings that affect the behavior of the content moderation system.
            JSONObject contentModeration = new JSONObject();
            // Accepted values: "auto" "low"
            contentModeration.put("publicFigureThreshold", StringUtils.blankToDefault(modelParam.getString("publicFigureThreshold"), "auto"));
            paramMap.put("contentModeration", contentModeration);
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("textToImage Runway ============================ request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_RUNWAY) + RUNWAY_TEXT_TO_IMAGE) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_RUNWAY)) //
                    .addHeader(RUNWAY_HEADER_X_RUNWAY_VERSION, RUNWAY_HEADER_X_RUNWAY_VERSION_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userToken, res, response);
        } catch (Exception e) {
            log.error("textToImage Runway ===================== exception:{}", e.getMessage());
        }
    }

    @Override
    public void video(CommonModelBuilder builder, HttpServletResponse response) {
        String modelFunction = builder.getUserModelUsage().getModelFunction();
        if (StringUtils.containsIgnoreCase(modelFunction, IMAGE_TO_VIDEO.getValue())) {
            imageToVideo(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, UPSCALE_VIDEO.getValue())) {
            videoUpscale(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, VIDEO_CHARACTER_PERFORMANCE.getValue())) {
            characterPerformance(builder, response);
        }
    }

    /**
     * <a href="https://docs.dev.runwayml.com/api/#tag/Start-generating">...</a>
     */
    private void imageToVideo(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            paramMap.put("model", builder.getModelName());
            // string or Array of PromptImage {uri,position}
            paramMap.put("promptImage", modelParam.get("promptImage"));
            // "1280:720" "720:1280" "1104:832" "832:1104" "960:960" "1584:672" "1280:768" "768:1280"
            paramMap.put("ratio", StringUtils.blankToDefault(modelParam.getString("ratio"), "1280:720"));
            // [ 0 .. 4294967295 ]
            paramMap.put("seed", modelParam.getInteger("seed"));
            // string <= 1000 characters
            paramMap.put("promptText", modelParam.getString("promptText"));
            // Accepted values: 5 10
            paramMap.put("duration", modelParam.getInteger("duration"));
            // Settings that affect the behavior of the content moderation system.
            JSONObject contentModeration = new JSONObject();
            // Accepted values: "auto" "low"
            contentModeration.put("publicFigureThreshold", StringUtils.blankToDefault(modelParam.getString("publicFigureThreshold"), "auto"));
            paramMap.put("contentModeration", contentModeration);
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("imageToVideo ============================ Runway request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_RUNWAY) + RUNWAY_IMAGE_TO_VIDEO) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_RUNWAY)) //
                    .addHeader(RUNWAY_HEADER_X_RUNWAY_VERSION, RUNWAY_HEADER_X_RUNWAY_VERSION_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userToken, res, response);
        } catch (Exception e) {
            log.error("imageToVideo Runway ===================== exception:{}", e.getMessage());
        }
    }

    /**
     * <a href="https://docs.dev.runwayml.com/api/#tag/Start-generating/paths/~1v1~1video_upscale/post">...</a>
     */
    private void videoUpscale(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            paramMap.put("model", builder.getModelName());
            // HTTPS URL pointing to a video or a data URI containing a video. The video must be less than 4096px on each side. The video duration may not exceed 40 seconds. See our docs on video inputs for more information.
            paramMap.put("videoUri", modelParam.getString("videoUri"));
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("videoUpscale Runway ============================ Runway request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_RUNWAY) + RUNWAY_VIDEO_UPSCALE) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_RUNWAY)) //
                    .addHeader(RUNWAY_HEADER_X_RUNWAY_VERSION, RUNWAY_HEADER_X_RUNWAY_VERSION_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userToken, res, response);
        } catch (Exception e) {
            log.error("videoUpscale Runway ===================== exception:{}", e.getMessage());
        }
    }

    /**
     * <a href="https://docs.dev.runwayml.com/api/#tag/Start-generating/paths/~1v1~1character_performance/post">...</a>
     */
    private void characterPerformance(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            paramMap.put("model", builder.getModelName());

            JSONObject character = new JSONObject();
            // video or image
            character.put("type", modelParam.getString("character_type"));
            // A HTTPS URL
            character.put("uri", modelParam.getString("character_uri"));
            paramMap.put("character", character);

            JSONObject reference = new JSONObject();
            reference.put("type", "video");
            // A HTTPS URL
            reference.put("uri", modelParam.getString("reference_uri"));
            paramMap.put("reference", reference);

            // "1280:720" "720:1280" "960:960" "1104:832" "832:1104" "1584:672"
            paramMap.put("ratio", StringUtils.blankToDefault(modelParam.getString("ratio"), "1280:720"));
            // Default: true A boolean indicating whether to enable body control. When enabled, non-facial movements and gestures will be applied to the character in addition to facial expressions.
            paramMap.put("bodyControl", modelParam.getBooleanValue("bodyControl", true));
            // Default: 3 An integer between 1 and 5 (inclusive). A larger value increases the intensity of the character's expression.
            paramMap.put("expressionIntensity", modelParam.getIntValue("expressionIntensity", 3));
            // [ 0 .. 4294967295 ]
            paramMap.put("seed", modelParam.getInteger("seed"));
            // Settings that affect the behavior of the content moderation system.
            JSONObject contentModeration = new JSONObject();
            // Accepted values: "auto" "low"
            contentModeration.put("publicFigureThreshold", StringUtils.blankToDefault(modelParam.getString("publicFigureThreshold"), "auto"));
            paramMap.put("contentModeration", contentModeration);
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("characterPerformance Runway ============================ Runway request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_RUNWAY) + RUNWAY_CHARACTER_PERFORMANCE) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_RUNWAY)) //
                    .addHeader(RUNWAY_HEADER_X_RUNWAY_VERSION, RUNWAY_HEADER_X_RUNWAY_VERSION_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userToken, res, response);
        } catch (Exception e) {
            log.error("characterPerformance Runway ===================== exception:{}", e.getMessage());
        }
    }

    /**
     * 处理返回结果
     */
    private void dealTaskResponse(String baseUrl, String apiKey, UserModelUsage userToken, Response res, HttpServletResponse response) {
        try {
            if (res.isSuccessful() ) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("dealTaskResponse Runway ====================== data:{}", bodyStr);
                JSONObject bodyObject = JSONObject.parseObject(bodyStr);
                String taskId = bodyObject.getString("id");
                JSONObject taskResult;
                String status;
                for (int i = 0; i < 60; i++) {
                    ThreadUtil.sleep(3000);
                    taskResult = getTask(baseUrl, apiKey, taskId);
                    if (null == taskResult) {
                        break;
                    }
                    status = taskResult.getString("status");
                    if ("PENDING".equalsIgnoreCase(status) || "RUNNING".equalsIgnoreCase(status)) {
                        ThreadUtil.sleep(5000);
                        continue;
                    }
                    if ("SUCCEEDED".equalsIgnoreCase(status)) {
                        List<JSONObject> datalist = CollectionUtils.newArrayList();
                        JSONArray results = taskResult.getJSONArray("output");
                        for (int j = 0; j < results.size(); j++) {
                            JSONObject image = new JSONObject();
                            image.put("type", "url");
                            image.put("url", results.getString(j));
                            datalist.add(image);
                        }

                        Date end = new Date();
                        long costTime = end.getTime() - userToken.getRequestTime().getTime();
                        userToken.setResponseTime(end);
                        userToken.setDurationSeconds(NumberUtils.div(String.valueOf(costTime), "1000", 2));
                        userToken.setTotalCount(datalist.size());
                        userToken.setResponseBody(taskResult.toJSONString());
                        userToken.setAssistantMessage(JSON.toJSONString(datalist));
                        DisruptorData disruptorData = new DisruptorData();
                        disruptorData.setServiceName(USER_IMAGE_SERVICE);
                        disruptorData.setData(userToken);
                        disruptorProducer.pushData(disruptorData);

                        PrintWriter writer = response.getWriter();
                        writer.write(JSON.toJSONString(R.success(datalist)));
                        writer.flush();
                        break;
                    }
                    break;
                }
            } else {
                response.setStatus(res.code());
                PrintWriter writer = response.getWriter();
                if (null != res.body()) {
                    writer.write(JSON.toJSONString(R.error(new String(res.body().bytes(), StandardCharsets.UTF_8))));
                }
                writer.flush();
                log.error("dealTaskResponse Runway ================= code:{},response:{}", res.code(), res.message());
            }
        } catch (Exception e) {
            log.error("dealTaskResponse Runway ===================== exception:{}", e.getMessage());
        }
    }

    private JSONObject getTask(String baseUrl, String apiKey, String taskId) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(20, TimeUnit.SECONDS) // 20秒
                    .readTimeout(30, TimeUnit.SECONDS) // 30秒
                    .writeTimeout(30, TimeUnit.SECONDS) // 30秒
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(baseUrl, API_URL_RUNWAY) + RUNWAY_TASKS + taskId) //
                    .get() //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(apiKey, API_KEY_RUNWAY)) //
                    .addHeader(RUNWAY_HEADER_X_RUNWAY_VERSION, RUNWAY_HEADER_X_RUNWAY_VERSION_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            if (res.isSuccessful() ) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("getTask Runway ====================== data:{}", bodyStr);
                return JSONObject.parseObject(bodyStr);
            } else {
                log.error("getTask Runway ===================== code:{},response:{}", res.code(), res.message());
            }
        } catch (Exception e) {
            log.error("getTask Runway ===================== exception:{}", e.getMessage());
        }
        return null;
    }

}
