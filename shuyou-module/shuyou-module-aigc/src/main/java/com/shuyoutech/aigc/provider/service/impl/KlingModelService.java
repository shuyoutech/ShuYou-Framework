package com.shuyoutech.aigc.provider.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
import java.util.HashMap;
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
 * <a href="http://klingai.com/">...</a>
 *
 * @author YangChao
 * @date 2025-07-29 16:00
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class KlingModelService implements ModelService {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.KLING.getValue();
    }

    /**
     * <a href="https://app.klingai.com/cn/dev/document-api/apiReference/model/imageGeneration">...</a>
     */
    @Override
    public void image(CommonModelBuilder builder, HttpServletResponse response) {
        String modelFunction = builder.getUserModelUsage().getModelFunction();
        if (StringUtils.containsIgnoreCase(modelFunction, TEXT_TO_IMAGE.getValue()) //
                || StringUtils.containsIgnoreCase(modelFunction, IMAGE_TO_IMAGE.getValue())) {
            // 图像生成
            imageGeneration(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, IMAGE_VIRTUAL_TRY_ON.getValue())) {
            // 虚拟试穿
            imageVirtualTryOn(builder, response);
        }
    }

    private void imageGeneration(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            paramMap.put("model_name", builder.getModelName());
            // 正向文本提示词
            paramMap.put("prompt", modelParam.getString("prompt"));
            // 负向文本提示词
            paramMap.put("negative_prompt", modelParam.getString("negative_prompt"));
            // 参考图像 支持传入图片Base64编码或图片URL
            paramMap.put("image", modelParam.getString("image"));
            // 图片参考类型 枚举值：subject（角色特征参考）, face（人物长相参考） 使用face（人物长相参考）时，上传图片需仅含1张人脸。 使用 kling-v1-5 且 image 参数不为空时，当前参数必填 仅 kling-v1-5 支持当前参数
            paramMap.put("image_reference", modelParam.getString("image_reference"));
            // 生成过程中对用户上传图片的参考强度 取值范围：[0,1]，数值越大参考强度越大
            paramMap.put("image_fidelity", modelParam.getDouble("image_fidelity"));
            // 面部参考强度，即参考图中人物五官相似度 仅 image_reference 参数为 subject 时生效 取值范围：[0,1]，数值越大参考强度越大
            paramMap.put("human_fidelity", modelParam.getDouble("human_fidelity"));
            // 生成图片数量 取值范围：[1,9]
            paramMap.put("n", modelParam.getIntValue("n", 1));
            // 生成图片的画面纵横比（宽:高） 枚举值：16:9, 9:16, 1:1, 4:3, 3:4, 3:2, 2:3, 21:9
            paramMap.put("aspect_ratio", modelParam.getString("aspect_ratio"));
            // 本次任务结果回调通知地址，如果配置，服务端会在任务状态发生变更时主动通知
            paramMap.put("callback_url", modelParam.getString("callback_url"));
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("imageGeneration Kling ============================ request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            String apiKey = builder.getApiKey();
            String url = StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_KLING) + KLING_IMAGES_GENERATIONS;
            Request request = new Request.Builder() //
                    .url(url) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + getToken(StringUtils.blankToDefault(apiKey, API_KEY_KLING))) //
                    .build();

            Response res = client.newCall(request).execute();
            dealImageTaskResponse(url, apiKey, userToken, res, response);
        } catch (Exception e) {
            log.error("imageGeneration Kling ===================== exception:{}", e.getMessage());
        }
    }

    private void imageVirtualTryOn(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            paramMap.put("model_name", builder.getModelName());
            // 上传的人物图片
            paramMap.put("human_image", modelParam.getString("human_image"));
            // 虚拟试穿的服饰图片
            paramMap.put("cloth_image", modelParam.getString("cloth_image"));
            // 本次任务结果回调通知地址，如果配置，服务端会在任务状态发生变更时主动通知
            paramMap.put("callback_url", modelParam.getString("callback_url"));
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("imageVirtualTryOn Kling ============================ request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            String apiKey = builder.getApiKey();
            String url = StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_KLING) + KLING_IMAGES_VIRTUAL_TRY_ON;
            Request request = new Request.Builder() //
                    .url(url) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + getToken(StringUtils.blankToDefault(apiKey, API_KEY_KLING))) //
                    .build();

            Response res = client.newCall(request).execute();
            dealImageTaskResponse(url, apiKey, userToken, res, response);
        } catch (Exception e) {
            log.error("imageVirtualTryOn Kling ===================== exception:{}", e.getMessage());
        }
    }

    @Override
    public void video(CommonModelBuilder builder, HttpServletResponse response) {
        String modelFunction = builder.getUserModelUsage().getModelFunction();
        if (StringUtils.containsIgnoreCase(modelFunction, TEXT_TO_VIDEO.getValue())) {
            // 视频生成-文生视频
            text2video(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, IMAGE_TO_VIDEO.getValue())) {
            // 视频生成-图生视频
            image2video(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, MULTI_IMAGE_TO_VIDEO.getValue())) {
            // 视频生成-多图参考生视频
            multiImage2video(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, VIDEO_EXTEND.getValue())) {
            // 视频生成-视频延长
            videoExtend(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, VIDEO_LIP_SYNC.getValue())) {
            // 视频生成-对口型
            videoLipSync(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, VIDEO_EFFECTS.getValue())) {
            // 视频特效
            videoEffects(builder, response);
        }
    }

    private void text2video(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // kling-v1, kling-v1-6, kling-v2-master, kling-v2-1-master
            paramMap.put("model_name", builder.getModelName());
            // 正向文本提示词 不能超过2500个字
            paramMap.put("prompt", modelParam.getString("prompt"));
            // 负向文本提示词
            paramMap.put("negative_prompt", modelParam.getString("negative_prompt"));
            // 生成视频的自由度；值越大，模型自由度越小，与用户输入的提示词相关性越强 取值范围：[0, 1]
            paramMap.put("cfg_scale", modelParam.getDouble("cfg_scale"));
            // 生成视频的模式 枚举值：std，pro
            paramMap.put("mode", modelParam.getString("mode"));
            // 控制摄像机运动的协议
            paramMap.put("camera_control", modelParam.get("camera_control"));
            // 生成视频的画面纵横比（宽:高） 枚举值：16:9, 9:16, 1:1
            paramMap.put("aspect_ratio", modelParam.getString("aspect_ratio"));
            // 生成视频时长，单位s 枚举值：5，10
            paramMap.put("duration", modelParam.getString("duration"));
            // 本次任务结果回调通知地址，如果配置，服务端会在任务状态发生变更时主动通知
            paramMap.put("callback_url", modelParam.getString("callback_url"));
            // 自定义任务ID
            paramMap.put("external_task_id", modelParam.getString("external_task_id"));
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("text2video Kling ============================ request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            String apiKey = builder.getApiKey();
            String url = StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_KLING) + KLING_VIDEOS_TEXT2VIDEO;
            Request request = new Request.Builder() //
                    .url(url) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + getToken(StringUtils.blankToDefault(apiKey, API_KEY_KLING))) //
                    .build();

            Response res = client.newCall(request).execute();
            dealVideoTaskResponse(url, apiKey, userToken, res, response);
        } catch (Exception e) {
            log.error("text2video Kling ===================== exception:{}", e.getMessage());
        }
    }

    private void image2video(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // kling-v1, kling-v1-5, kling-v1-6, kling-v2-master, kling-v2-1, kling-v2-1-master
            paramMap.put("model_name", builder.getModelName());
            // 支持传入图片Base64编码或图片URL
            paramMap.put("image", modelParam.getString("image"));
            // 参考图像 - 尾帧控制 支持传入图片Base64编码或图片URL
            paramMap.put("image_tail", modelParam.getString("image_tail"));
            // 正向文本提示词 不能超过2500个字
            paramMap.put("prompt", modelParam.getString("prompt"));
            // 负向文本提示词
            paramMap.put("negative_prompt", modelParam.getString("negative_prompt"));
            // 生成视频的自由度；值越大，模型自由度越小，与用户输入的提示词相关性越强 取值范围：[0, 1]
            paramMap.put("cfg_scale", modelParam.getDouble("cfg_scale"));
            // 生成视频的模式 枚举值：std，pro
            paramMap.put("mode", modelParam.getString("mode"));
            // 静态笔刷涂抹区域（用户通过运动笔刷涂抹的 mask 图片
            paramMap.put("static_mask", modelParam.getString("static_mask"));
            // 动态笔刷配置列表
            paramMap.put("dynamic_masks", modelParam.get("dynamic_masks"));
            // 控制摄像机运动的协议
            paramMap.put("camera_control", modelParam.get("camera_control"));
            // 生成视频时长，单位s 枚举值：5，10
            paramMap.put("duration", modelParam.getString("duration"));
            // 本次任务结果回调通知地址，如果配置，服务端会在任务状态发生变更时主动通知
            paramMap.put("callback_url", modelParam.getString("callback_url"));
            // 自定义任务ID
            paramMap.put("external_task_id", modelParam.getString("external_task_id"));
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("image2video Kling ============================ request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            String apiKey = builder.getApiKey();
            String url = StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_KLING) + KLING_VIDEOS_IMAGE2VIDEO;
            Request request = new Request.Builder() //
                    .url(url) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + getToken(StringUtils.blankToDefault(apiKey, API_KEY_KLING))) //
                    .build();

            Response res = client.newCall(request).execute();
            dealVideoTaskResponse(url, apiKey, userToken, res, response);
        } catch (Exception e) {
            log.error("image2video Kling ===================== exception:{}", e.getMessage());
        }
    }

    private void multiImage2video(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // kling-v1, kling-v1-5, kling-v1-6, kling-v2-master, kling-v2-1, kling-v2-1-master
            paramMap.put("model_name", builder.getModelName());
            // 最多支持4张图片 "image_list":[ { "image":"image_url"  } ]
            paramMap.put("image_list", modelParam.get("image_list"));
            // 正向文本提示词 不能超过2500个字
            paramMap.put("prompt", modelParam.getString("prompt"));
            // 负向文本提示词
            paramMap.put("negative_prompt", modelParam.getString("negative_prompt"));
            // 生成视频的模式 枚举值：std，pro
            paramMap.put("mode", modelParam.getString("mode"));
            // 生成视频时长，单位s 枚举值：5，10
            paramMap.put("duration", modelParam.getString("duration"));
            // 生成图片的画面纵横比（宽:高） 枚举值：16:9, 9:16, 1:1
            paramMap.put("aspect_ratio", modelParam.getString("aspect_ratio"));
            // 本次任务结果回调通知地址，如果配置，服务端会在任务状态发生变更时主动通知
            paramMap.put("callback_url", modelParam.getString("callback_url"));
            // 自定义任务ID
            paramMap.put("external_task_id", modelParam.getString("external_task_id"));
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("multiImage2video Kling ============================ request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            String apiKey = builder.getApiKey();
            String url = StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_KLING) + KLING_VIDEOS_MULTI_IMAGE2VIDEO;
            Request request = new Request.Builder() //
                    .url(url) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + getToken(StringUtils.blankToDefault(apiKey, API_KEY_KLING))) //
                    .build();

            Response res = client.newCall(request).execute();
            dealVideoTaskResponse(url, apiKey, userToken, res, response);
        } catch (Exception e) {
            log.error("multiImage2video Kling ===================== exception:{}", e.getMessage());
        }
    }

    private void videoExtend(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // kling-v1, kling-v1-5, kling-v1-6, kling-v2-master, kling-v2-1, kling-v2-1-master
            paramMap.put("model_name", builder.getModelName());
            // 视频ID 支持通过文本、图片和视频延长生成的视频的ID（原视频不能超过3分钟）
            paramMap.put("video_id", modelParam.getString("video_id"));
            // 正向文本提示词 不能超过2500个字
            paramMap.put("prompt", modelParam.getString("prompt"));
            // 负向文本提示词
            paramMap.put("negative_prompt", modelParam.getString("negative_prompt"));
            // 提示词参考强度
            paramMap.put("cfg_scale", modelParam.getDouble("cfg_scale"));
            // 本次任务结果回调通知地址，如果配置，服务端会在任务状态发生变更时主动通知
            paramMap.put("callback_url", modelParam.getString("callback_url"));
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("videoExtend Kling ============================ request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            String apiKey = builder.getApiKey();
            String url = StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_KLING) + KLING_VIDEOS_VIDEO_EXTEND;
            Request request = new Request.Builder() //
                    .url(url) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + getToken(StringUtils.blankToDefault(apiKey, API_KEY_KLING))) //
                    .build();

            Response res = client.newCall(request).execute();
            dealVideoTaskResponse(url, apiKey, userToken, res, response);
        } catch (Exception e) {
            log.error("videoExtend Kling ===================== exception:{}", e.getMessage());
        }
    }

    private void videoLipSync(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 包含多个字段，用于指定视频、口型对应内容等
            JSONObject input = new JSONObject();
            // 通过可灵AI生成的视频的ID。
            input.put("video_id", modelParam.getString("video_id"));
            // 所上传视频的获取链接
            input.put("video_url", modelParam.getString("video_url"));
            // 生成视频的模式
            input.put("mode", modelParam.getString("mode"));
            // 生成对口型视频的文本内容
            input.put("text", modelParam.getString("text"));
            // 音色ID
            input.put("voice_id", modelParam.getString("voice_id"));
            // 音色语种，与音色ID对应，详见
            input.put("voice_language", modelParam.getString("voice_language"));
            // 语速 有效范围：0.8~2.0，精确至小数点后1位，超出部分将
            input.put("voice_speed", modelParam.getDouble("voice_speed"));
            // 使用音频文件生成对口型视频时，传输音频文件的方式 枚举值：file，url
            input.put("audio_type", modelParam.getString("audio_type"));
            // 音频文件本地路径
            input.put("audio_file", modelParam.getString("audio_file"));
            // 音频文件下载url
            input.put("audio_url", modelParam.getString("audio_url"));
            paramMap.put("input", input);
            // 本次任务结果回调通知地址，如果配置，服务端会在任务状态发生变更时主动通知
            paramMap.put("callback_url", modelParam.getString("callback_url"));
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("videoLipSync Kling ============================ request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            String apiKey = builder.getApiKey();
            String url = StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_KLING) + KLING_VIDEOS_VIDEO_LIP_SYNC;
            Request request = new Request.Builder() //
                    .url(url) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + getToken(StringUtils.blankToDefault(apiKey, API_KEY_KLING))) //
                    .build();

            Response res = client.newCall(request).execute();
            dealVideoTaskResponse(url, apiKey, userToken, res, response);
        } catch (Exception e) {
            log.error("videoLipSync Kling ===================== exception:{}", e.getMessage());
        }
    }

    private void videoEffects(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 场景名称 枚举值：bloombloom, dizzydizzy, fuzzyfuzzy, squish, expansion, hug, kiss, heart_gesture
            paramMap.put("effect_scene", modelParam.getString("effect_scene"));
            // 支持不同任务输入的结构体 根据scene不同，结构体里传的字段不同，具体如「场景请求体」所示
            JSONObject input = new JSONObject();
            // 模型名称
            input.put("model_name", modelParam.getString("model_name"));
            // 参考图像 支持传入图片Base64编码或图片URL（确保可访问）
            input.put("image", modelParam.getString("image"));
            // 生成视频时长，单位s
            input.put("duration", modelParam.getString("duration"));
            // 生成视频的模式 枚举值：std，pro
            input.put("mode", modelParam.getString("mode"));
            // 参考图像组
            input.put("images", modelParam.get("images"));
            paramMap.put("input", input);
            // 本次任务结果回调通知地址，如果配置，服务端会在任务状态发生变更时主动通知
            paramMap.put("callback_url", modelParam.getString("callback_url"));
            // 自定义任务ID
            paramMap.put("external_task_id", modelParam.getString("external_task_id"));
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("videoEffects Kling ============================ request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            String apiKey = builder.getApiKey();
            String url = StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_KLING) + KLING_VIDEOS_VIDEO_EFFECTS;
            Request request = new Request.Builder() //
                    .url(url) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + getToken(StringUtils.blankToDefault(apiKey, API_KEY_KLING))) //
                    .build();

            Response res = client.newCall(request).execute();
            dealVideoTaskResponse(url, apiKey, userToken, res, response);
        } catch (Exception e) {
            log.error("videoEffects Kling ===================== exception:{}", e.getMessage());
        }
    }

    /**
     * 处理返回结果
     */
    private void dealImageTaskResponse(String url, String apiKey, UserModelUsage userToken, Response res, HttpServletResponse response) {
        try {
            if (res.isSuccessful() ) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("dealImageTaskResponse Kling ====================== data:{}", bodyStr);
                JSONObject bodyObject = JSONObject.parseObject(bodyStr);
                String taskId = bodyObject.getJSONObject("data").getString("task_id");
                JSONObject taskResult;
                String status;
                for (int i = 0; i < 60; i++) {
                    ThreadUtil.sleep(3000);
                    taskResult = getTask(url, apiKey, taskId);
                    if (null == taskResult) {
                        break;
                    }
                    status = taskResult.getJSONObject("data").getString("task_status");
                    if ("submitted".equalsIgnoreCase(status) || "processing".equalsIgnoreCase(status)) {
                        ThreadUtil.sleep(5000);
                        continue;
                    }
                    if ("succeed".equalsIgnoreCase(status)) {
                        List<JSONObject> datalist = CollectionUtils.newArrayList();
                        JSONArray results = taskResult.getJSONObject("data").getJSONObject("task_result").getJSONArray("images");
                        for (int j = 0; j < results.size(); j++) {
                            JSONObject image = new JSONObject();
                            image.put("type", "url");
                            image.put("url", results.getJSONObject(j).getString("url"));
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
                writer.write(JSON.toJSONString(R.error(new String(res.body().bytes(), StandardCharsets.UTF_8))));
                writer.flush();
                log.error("dealImageTaskResponse Kling ================= code:{},response:{}", res.code(), res.message());
            }
        } catch (Exception e) {
            log.error("dealImageTaskResponse Kling ===================== exception:{}", e.getMessage());
        }
    }

    /**
     * 处理返回结果
     */
    private void dealVideoTaskResponse(String url, String apiKey, UserModelUsage userToken, Response res, HttpServletResponse response) {
        try {
            if (res.isSuccessful() ) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("dealVideoTaskResponse Kling ====================== data:{}", bodyStr);
                JSONObject bodyObject = JSONObject.parseObject(bodyStr);
                String taskId = bodyObject.getJSONObject("data").getString("task_id");
                JSONObject taskResult;
                String taskStatus;

                for (int i = 0; i < 60; i++) {
                    ThreadUtil.sleep(3000);
                    taskResult = getTask(url, apiKey, taskId);
                    if (null == taskResult) {
                        break;
                    }
                    taskStatus = taskResult.getJSONObject("data").getString("task_status");
                    // 枚举值：submitted（已提交）、processing（处理中）、succeed（成功）、failed（失败）
                    if ("submitted".equalsIgnoreCase(taskStatus) || "processing".equalsIgnoreCase(taskStatus)) {
                        ThreadUtil.sleep(5000);
                        continue;
                    }
                    if ("succeed".equalsIgnoreCase(taskStatus)) {
                        List<JSONObject> datalist = CollectionUtils.newArrayList();
                        JSONArray results = taskResult.getJSONObject("data").getJSONObject("task_result").getJSONArray("videos");
                        for (int j = 0; j < results.size(); j++) {
                            JSONObject video = new JSONObject();
                            video.put("type", "url");
                            video.put("id", results.getJSONObject(j).getString("id"));
                            video.put("url", results.getJSONObject(j).getString("url"));
                            video.put("duration", results.getJSONObject(j).getString("duration"));
                            datalist.add(video);
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
                writer.write(JSON.toJSONString(R.error(new String(res.body().bytes(), StandardCharsets.UTF_8))));
                writer.flush();
                log.error("dealVideoTaskResponse Kling ================= code:{},response:{}", res.code(), res.message());
            }
        } catch (Exception e) {
            log.error("dealVideoTaskResponse Kling ===================== exception:{}", e.getMessage());
        }
    }

    private JSONObject getTask(String url, String apiKey, String taskId) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(20, TimeUnit.SECONDS) // 20秒
                    .readTimeout(30, TimeUnit.SECONDS) // 30秒
                    .writeTimeout(30, TimeUnit.SECONDS) // 30秒
                    .build();

            Request request = new Request.Builder() //
                    .url(url + "/" + taskId) //
                    .get() //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + getToken(StringUtils.blankToDefault(apiKey, API_KEY_KLING))) //
                    .build();

            Response res = client.newCall(request).execute();
            if (res.isSuccessful()) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("getTask Kling ====================== data:{}", bodyStr);
                if (!JSON.isValidObject(bodyStr)) {
                    log.error("getTask Kling ================= response body:{}", bodyStr);
                    return null;
                }
                return JSONObject.parseObject(bodyStr);
            }
        } catch (Exception e) {
            log.error("getTask Kling ===================== exception:{}", e.getMessage());
        }
        return null;
    }

    private String getToken(String apiKey) {
        String accessKey = StringUtils.subBefore(apiKey, "-", false);
        String secretKey = StringUtils.subAfter(apiKey, "-", false);
        // 有效时间，此处示例代表当前时间+1800s(30min)
        Date expiredAt = new Date(System.currentTimeMillis() + 1800 * 1000);
        // 开始生效的时间，此处示例代表当前时间-5秒
        Date notBefore = new Date(System.currentTimeMillis() - 5 * 1000);
        Algorithm algo = Algorithm.HMAC256(secretKey);
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        return JWT.create().withIssuer(accessKey).withHeader(header).withExpiresAt(expiredAt).withNotBefore(notBefore).sign(algo);
    }

}
