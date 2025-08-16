package com.shuyoutech.aigc.provider.service.impl;

import cn.hutool.core.thread.ThreadUtil;
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
import org.springframework.http.HttpHeaders;
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
 * <a href="https://help.aliyun.com/zh/model-studio/model-api-reference/"></a>
 *
 * @author YangChao
 * @date 2025-07-13 20:18
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AliyunModelService implements ModelService {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.ALIYUN.getValue();
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
            if (StringUtils.isNotBlank(modelParam.getString("prompt"))) {
                messages.add(ChatMessage.builder().role(ROLE_SYSTEM).content(modelParam.getString("prompt")).build());
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

            // 输出数据的模态，仅支持 Qwen-Omni 模型指定。可选值 ["text","audio"]：输出文本与音频； ["text"]：输出文本。
            paramMap.put("modalities", modelParam.get("modalities"));
            // 输出音频的音色与格式，仅支持 Qwen-Omni 模型，且modalities参数需要包含"audio"
            paramMap.put("audio", modelParam.get("audio"));
            // 采样温度，控制模型生成文本的多样性。 temperature越高，生成的文本更多样，反之，生成的文本更确定。取值范围： [0, 2)
            paramMap.put("temperature", modelParam.get("temperature"));
            // 核采样的概率阈值，控制模型生成文本的多样性。 top_p越高，生成的文本更多样。反之，生成的文本更确定。 取值范围：（0,1.0]
            paramMap.put("top_p", modelParam.get("top_p"));
            // 生成过程中采样候选集的大小。例如，取值为50时，仅将单次生成中得分最高的50个Token组成随机采样的候选集。取值越大，生成的随机性越高；
            // 取值越小，生成的确定性越高。取值为None或当top_k大于100时，表示不启用top_k策略，此时仅有top_p策略生效。 取值需要大于或等于0。
            paramMap.put("top_k", modelParam.get("top_k"));
            // 控制模型生成文本时的内容重复度。 取值范围：[-2.0, 2.0]。正数会减少重复度，负数会增加重复度。 适用场景：
            // 较高的presence_penalty适用于要求多样性、趣味性或创造性的场景，如创意写作或头脑风暴。
            // 较低的presence_penalty适用于要求一致性或专业术语的场景，如技术文档或其他正式文档。
            paramMap.put("presence_penalty", modelParam.get("presence_penalty"));
            // 返回内容的格式。可选值：{"type": "text"}或{"type": "json_object"}。设置为{"type": "json_object"}时会输出标准格式的JSON字符串。使用方法请参见：结构化输出。
            // 如果指定该参数为{"type": "json_object"}，您需要在System Message或User Message中指引模型输出JSON格式，如：“请按照json格式输出。”
            paramMap.put("response_format", modelParam.get("response_format"));
            // 本次请求返回的最大 Token 数。
            paramMap.put("max_tokens", modelParam.get("max_tokens"));
            // 生成响应的个数，取值范围是1-4。对于需要生成多个响应的场景（如创意写作、广告文案等），可以设置较大的 n 值。
            // 当前仅支持 qwen-plus 与 Qwen3（非思考模式） 模型，且在传入 tools 参数时固定为1。
            // 设置较大的 n 值不会增加输入 Token 消耗，会增加输出 Token 的消耗。
            paramMap.put("n", modelParam.get("n"));
            // 是否开启思考模式，适用于 Qwen3 模型。 Qwen3 商业版模型默认值为 False，Qwen3 开源版模型默认值为 True。
            paramMap.put("enable_thinking", modelParam.get("enable_thinking"));
            // 思考过程的最大长度，只在enable_thinking为true时生效。适用于 Qwen3 的商业版与开源版模型。详情请参见限制思考长度。
            paramMap.put("thinking_budget", modelParam.get("thinking_budget"));
            // 设置seed参数会使文本生成过程更具有确定性，通常用于使模型每次运行的结果一致。 在每次模型调用时传入相同的seed值（由您指定），并保持其他参数不变，模型将尽可能返回相同的结果。 取值范围：0到231−1。
            paramMap.put("seed", modelParam.get("seed"));
            // 是否返回输出 Token 的对数概率，可选值：true 返回； false 不返回
            paramMap.put("logprobs", modelParam.get("logprobs"));
            // 指定在每一步生成时，返回模型最大概率的候选 Token 个数。 取值范围：[0,5] 仅当 logprobs 为 true 时生效。
            paramMap.put("top_logprobs", modelParam.get("top_logprobs"));
            // 使用stop参数后，当模型生成的文本即将包含指定的字符串或token_id时，将自动停止生成。
            paramMap.put("stop", modelParam.get("stop"));
            // 可供模型调用的工具数组，可以包含一个或多个工具对象。一次Function Calling流程模型会从中选择一个工具（开启parallel_tool_calls可以选择多个工具）
            paramMap.put("tools", modelParam.get("tools"));
            // 如果您希望对于某一类问题，大模型能够采取制定好的工具选择策略（如强制使用某个工具、强制不使用工具），可以通过修改tool_choice参数来强制指定工具调用的策略
            paramMap.put("tool_choice", modelParam.get("tool_choice"));
            // 是否开启并行工具调用。参数为true时开启，为false时不开启。并行工具调用详情请参见：并行工具调用。
            paramMap.put("parallel_tool_calls", modelParam.get("parallel_tool_calls"));
            // 当您使用翻译模型时需要配置的翻译参数。
            paramMap.put("translation_options", modelParam.get("translation_options"));
            // 模型在生成文本时是否使用互联网搜索结果进行参考 true：启用互联网搜索
            paramMap.put("enable_search", modelParam.get("enable_search"));
            // 联网搜索的策略。仅当enable_search为true时生效。
            if (BooleanUtils.isTrue(modelParam.getBoolean("enable_search"))) {
                JSONObject searchOptions = new JSONObject();
                searchOptions.put("forced_search", true);
                searchOptions.put("search_strategy", "standard");
                paramMap.put("search_options", searchOptions);
            }
            String requestBody = JSONObject.toJSONString(paramMap);
            log.info("chat ============================ Aliyun request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ALIYUN) + ALIYUN_CHAT_COMPLETIONS) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ALIYUN)) //
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
                log.error("chat Aliyun ============================ getCountDownLatch timed out");
            }
        } catch (Exception e) {
            log.error("chat Aliyun ===================== exception:{}", e.getMessage());
        }
    }

    @Override
    public void image(CommonModelBuilder builder, HttpServletResponse response) {
        String modelFunction = builder.getUserModelUsage().getModelFunction();
        if (StringUtils.containsIgnoreCase(modelFunction, TEXT_TO_IMAGE.getValue())) {
            text2ImageSynthesis(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, IMAGE_EDIT.getValue()) //
                || StringUtils.containsIgnoreCase(modelFunction, IMAGE_TO_IMAGE.getValue())) {
            image2ImageSynthesis(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, IMAGE_VIRTUAL_TRY_ON.getValue())) {
            virtualModelGeneration(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, IMAGE_BACKGROUND.getValue())) {
            backgroundGeneration(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, WORDART_SEMANTIC.getValue())) {
            wordartSemantic(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, WORDART_TEXTURE.getValue())) {
            wordartTexture(builder, response);
        }
    }

    private void text2ImageSynthesis(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            String modelName = builder.getModelName();
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 模型名称
            paramMap.put("model", modelName);
            // 输入的基本信息，如提示词等
            JSONObject input = new JSONObject();
            // 正向提示词，用来描述生成图像中期望包含的元素和视觉特点。
            input.put("prompt", modelParam.getString("prompt"));
            // 反向提示词，用来描述不希望在画面中看到的内容，可以对画面进行限制。
            input.put("negative_prompt", modelParam.getString("negative_prompt"));
            // 输入参考图像的URL；图片格式可为 jpg，png，tiff，webp等常见位图格式。默认为空。
            input.put("init_image", modelParam.getString("init_image"));
            // 海报生成模式。
            input.put("generate_mode", modelParam.getString("generate_mode"));
            // 生成的海报数。该参数只在generate_mode=generate时有效。 取值范围：[1,4]，默认为1
            input.put("generate_num", modelParam.getInteger("generate_num"));
            // 当generate_mode为"sr"或"hrf"时为必选项。需要提升分辨率或者高清修复的海报图片对应的辅助参数，数量限制为1。
            input.put("auxiliary_parameters", modelParam.getString("auxiliary_parameters"));
            // 主标题。
            input.put("title", modelParam.getString("title"));
            // 副标题。
            input.put("sub_title", modelParam.getString("sub_title"));
            // 正文。
            input.put("body_text", modelParam.getString("body_text"));
            // 中文提示词。
            input.put("prompt_text_zh", modelParam.getString("prompt_text_zh"));
            // 英文提示词。
            input.put("prompt_text_en", modelParam.getString("prompt_text_en"));
            // 生成海报的版式。
            input.put("wh_ratios", modelParam.getString("wh_ratios"));
            // 海报风格名称。
            input.put("lora_name", modelParam.getString("lora_name"));
            // 海报风格权重，需要与lora_name参数配合使用。 取值范围：[0, 1]，默认值为0.8。
            input.put("lora_weight", modelParam.getDouble("lora_weight"));
            // 留白效果权重，用于控制海报留白效果。
            input.put("ctrl_ratio", modelParam.getDouble("ctrl_ratio"));
            // 留白步数比例，用于控制海报留白效果。取值范围：(0, 1]，默认值为0.7。
            input.put("ctrl_step", modelParam.getDouble("ctrl_step"));
            // 标题是否启用创意排版。默认值为false。
            input.put("creative_title_layout", modelParam.getBoolean("creative_title_layout"));
            paramMap.put("input", input);
            // 图像处理参数
            JSONObject parameters = new JSONObject();
            // 输出图像的分辨率。默认值是1024*1024
            parameters.put("size", modelParam.getString("size"));
            // 生成图片的数量。取值范围为1~4张，默认为4
            parameters.put("n", modelParam.getInteger("n"));
            // 随机数种子，用于控制模型生成内容的随机性。seed参数取值范围是[0, 2147483647
            parameters.put("seed", modelParam.getInteger("seed"));
            // 是否开启prompt智能改写。开启后会使用大模型对输入prompt进行智能改写，仅对正向提示词有效。对于较短的输入prompt生成效果提升明显，但会增加3-4秒耗时
            parameters.put("prompt_extend", modelParam.getBoolean("prompt_extend"));
            // 是否添加水印标识，水印位于图片右下角，文案为“AI生成”。
            parameters.put("watermark", modelParam.getBoolean("watermark"));
            // 去噪推理步数，一般步数越大，图像质量越高，步数越小，推理速度越快。 目前默认40，用户可以在1-500间进行调整。
            parameters.put("steps", modelParam.getInteger("steps"));
            // 用于指导生成的结果与用户输入的prompt的贴合程度，越高则生成结果与用户输入的prompt更相近。目前默认4.5，倾向于输入4～5内的值。
            parameters.put("cfg", modelParam.getDoubleValue("cfg"));
            // 偏移量，用于调整生成内容的某些特性或参数。默认为3.0。
            parameters.put("shift", modelParam.getDoubleValue("shift"));
            // 指导度量值，用于在图像生成过程中调整模型的创造性与文本指导的紧密度。较高的值会使得生成的图像更忠于文本提示，但可能减少多样性；较低的值则允许更多创造性，增加图像变化。默认值为3.5。
            parameters.put("guidance", modelParam.getDoubleValue("guidance"));
            // 一个布尔值，表示是否在采样过程中将部分计算密集型组件临时从GPU卸载到CPU，以减轻内存压力或提升效率。如果您的系统资源有限或希望加速采样过程，可以启用此选项，默认为False。
            parameters.put("offload", modelParam.getBoolean("offload"));
            // 一个布尔值，决定是否在输出的图像文件中嵌入生成时使用的提示文本等元数据信息。这对于后续跟踪或分享生成设置非常有用，默认为True。
            parameters.put("add_sampling_metadata", modelParam.getBoolean("add_sampling_metadata"));
            paramMap.put("parameters", parameters);
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("imageGeneration ============================ Aliyun request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ALIYUN) + ALIYUN_IMAGES_SYNTHESIS) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ALIYUN)) //
                    .addHeader(ALIYUN_HEADER_DASH_SCOPE_ASYNC, ALIYUN_HEADER_DASH_SCOPE_ASYNC_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userToken, res, response);
        } catch (Exception e) {
            log.error("imageGeneration Aliyun ===================== exception:{}", e.getMessage());
        }
    }

    private void virtualModelGeneration(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            String modelName = builder.getModelName();
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 模型名称
            paramMap.put("model", modelName);
            // 输入的基本信息，如提示词等
            JSONObject input = new JSONObject();
            // 原始真人展示图像URL地址。
            input.put("base_image_url", modelParam.getString("base_image_url"));
            // 对应原图的期望保留区域mask图URL，图片为（0,255）的黑白图，其中白色表示商品主体区域。
            input.put("mask_image_url", modelParam.getString("mask_image_url"));
            // 期望替换的人物图像URL地址。
            input.put("face_image_url", modelParam.getString("face_image_url"));
            // 对生成图像背景环境、模特的全身形象描述。
            input.put("prompt", modelParam.getString("prompt"));
            // 生成人像面部描述，支持中英文，小于100字符。
            input.put("face_prompt", modelParam.getString("face_prompt"));
            // 背景环境参考图像URL地址。
            input.put("background_image_url", modelParam.getString("background_image_url"));
            // 背景参考图像权重控制
            input.put("bgstyle_scale", modelParam.getDouble("bgstyle_scale"));
            // 输入图片是否是真人。
            input.put("realPerson", modelParam.getBoolean("realPerson"));
            // 生成图片风格。
            input.put("style", modelParam.getString("style"));
            // 控制生成seed。
            input.put("seed", modelParam.getInteger("seed"));
            // 生成图片长宽比例。
            input.put("aspect_ratio", modelParam.getString("aspect_ratio"));
            // 模板模特图片的URL地址。
            input.put("template_image_url", modelParam.getString("template_image_url"));
            // 鞋靴多视角图片URL地址。
            input.put("shoe_image_url", modelParam.get("shoe_image_url"));
            // 控制生成强度。 范围在[2.0,8.0]，默认为5.0，数值越大，颜色越鲜亮。
            input.put("scale", modelParam.getDoubleValue("scale"));
            paramMap.put("input", input);
            // 图像处理参数
            JSONObject parameters = new JSONObject();
            // 生成图片的数量。取值范围为1~4张，默认为4
            parameters.put("n", modelParam.getInteger("n"));
            // 指定生成的图像短边大小，单位：像素。生成图片和输入原图会保持相同的长宽比。
            parameters.put("short_side_size", modelParam.getString("short_side_size"));
            paramMap.put("parameters", parameters);
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("virtualModelGeneration ============================ Aliyun request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ALIYUN) + ALIYUN_IMAGES_VIRTUAL_MODEL) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ALIYUN)) //
                    .addHeader(ALIYUN_HEADER_DASH_SCOPE_ASYNC, ALIYUN_HEADER_DASH_SCOPE_ASYNC_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userToken, res, response);
        } catch (Exception e) {
            log.error("virtualModelGeneration Aliyun ===================== exception:{}", e.getMessage());
        }
    }

    private void backgroundGeneration(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            String modelName = builder.getModelName();
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 模型名称
            paramMap.put("model", modelName);
            // 输入的基本信息，如提示词等
            JSONObject input = new JSONObject();
            // 原始真人展示图像URL地址。
            input.put("base_image_url", modelParam.getString("base_image_url"));
            // 引导图像URL。
            input.put("ref_image_url", modelParam.getString("ref_image_url"));
            // 引导文本提示词，支持中英双语。它与ref_image_url参数至少填写一个。
            input.put("ref_prompt", modelParam.getString("ref_prompt"));
            // 负向提示词，描述画面不希望出现的内容。一般不填，使用模型内置的默认值。
            input.put("neg_ref_prompt", modelParam.getString("neg_ref_prompt"));
            // 边缘引导元素图像，包括前景元素图像列表和背景元素图像列表。
            input.put("reference_edge", modelParam.get("reference_edge"));
            paramMap.put("input", input);
            // 图像处理参数
            JSONObject parameters = new JSONObject();
            // 生成图片的数量。取值范围为1~4张，默认为4
            parameters.put("n", modelParam.getInteger("n"));
            // 模型版本。v2：旧版模型，速度快，默认值。 v3：新版模型，速度慢，但效果更好，推荐切换到最新版本v3。
            parameters.put("model_version", modelParam.getString("model_version"));
            // 当ref_image_url不为空时生效。该参数在图像引导的过程中添加随机变化，数值越大生成背景与引导图像的相关性越低，默认值300，取值范围[0,999]
            parameters.put("noise_level", modelParam.getInteger("noise_level"));
            // 仅当ref_image_url和ref_prompt同时输入时生效，表示引导文本prompt的权重。取值范围 [0,1]，默认值为0.5。
            parameters.put("ref_prompt_weight", modelParam.getDoubleValue("ref_prompt_weight"));
            paramMap.put("parameters", parameters);
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("backgroundGeneration ============================ Aliyun request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ALIYUN) + ALIYUN_BACKGROUND_GENERATION) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ALIYUN)) //
                    .addHeader(ALIYUN_HEADER_DASH_SCOPE_ASYNC, ALIYUN_HEADER_DASH_SCOPE_ASYNC_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userToken, res, response);
        } catch (Exception e) {
            log.error("backgroundGeneration Aliyun ===================== exception:{}", e.getMessage());
        }
    }

    private void wordartSemantic(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            String modelName = builder.getModelName();
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 模型名称
            paramMap.put("model", modelName);
            // 输入的基本信息，如提示词等
            JSONObject input = new JSONObject();
            // 用户输入的文字内容；
            input.put("text", modelParam.getString("text"));
            // 用户输入的文字内容
            input.put("prompt", modelParam.getString("prompt"));
            paramMap.put("input", input);
            // 图像处理参数
            JSONObject parameters = new JSONObject();
            // 生成的图片数量，默认为 4，取值范围为[1, 4]
            parameters.put("n", modelParam.getInteger("n"));
            // 变形迭代次数，数字越大文字变化程度越大 取值范围[10, 100]，默认30
            parameters.put("steps", modelParam.getInteger("steps"));
            // 指定需要使用的字体类型。不带该参数则使用默认字体，默认字体为方正楷体
            parameters.put("font_name", modelParam.getString("font_name"));
            // 用户传入的ttf文件；
            parameters.put("ttf_url", modelParam.getString("ttf_url"));
            // 图像比例，可选参数为：{"1280x720", "720x1280", "1024x1024"}，默认"1280x720"
            parameters.put("output_image_ratio", modelParam.getString("output_image_ratio"));
            paramMap.put("parameters", parameters);
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("wordartSemantic ============================ Aliyun request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ALIYUN) + ALIYUN_WORDART_SEMANTIC) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ALIYUN)) //
                    .addHeader(ALIYUN_HEADER_DASH_SCOPE_ASYNC, ALIYUN_HEADER_DASH_SCOPE_ASYNC_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userToken, res, response);
        } catch (Exception e) {
            log.error("wordartSemantic Aliyun ===================== exception:{}", e.getMessage());
        }
    }

    private void wordartTexture(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            String modelName = builder.getModelName();
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 模型名称
            paramMap.put("model", modelName);
            // 输入的基本信息，如提示词等
            JSONObject input = new JSONObject();
            // 图片输入的相关字段 image和text 需要二选一
            if (StringUtils.isNotBlank(modelParam.getString("text_content"))) {
                // 用户输入的文字内容；
                JSONObject text = new JSONObject();
                // 用户输入的文字内容，小于6个字；
                text.put("text_content", modelParam.getString("text_content"));
                // 用户传入的ttf文件；
                text.put("ttf_url", modelParam.getString("ttf_url"));
                // 使用预置字体的名称；
                text.put("font_name", modelParam.getString("font_name"));
                // 文字输入的图片的宽高比； 默认为"1:1"，可选的比例有："1:1", "16:9", "9:16"；
                text.put("output_image_ratio", modelParam.getString("output_image_ratio"));
                input.put("text", text);
            } else {
                JSONObject image = new JSONObject();
                // 文字图像的地址；
                image.put("image_url", modelParam.getString("image_url"));
                input.put("image", image);
            }
            // 期望文字纹理创意样式的描述提示词，长度小于200，不能为""
            input.put("prompt", modelParam.getString("prompt"));
            // 纹理风格的类型，包括“自定义”和“预设风格”两大类
            input.put("texture_style", modelParam.getString("texture_style"));
            // 风格参考图的地址
            input.put("ref_image_url", modelParam.getString("ref_image_url"));
            paramMap.put("input", input);
            // 图像处理参数
            JSONObject parameters = new JSONObject();
            // 生成的图片短边的长度，默认为704，取值范围为[512, 1024]， 若输入数值非64的倍数，则最终取值为不大于该数值的能被64整除的最大数；
            parameters.put("image_short_size", modelParam.getInteger("image_short_size"));
            // 生成的图片数量，默认为 1，取值范围为[1, 4]
            parameters.put("n", modelParam.getInteger("n"));
            // 是否返回带alpha通道的图片； 默认为 false；
            parameters.put("alpha_channel", modelParam.getBoolean("alpha_channel"));
            paramMap.put("parameters", parameters);
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("wordartTexture ============================ Aliyun request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ALIYUN) + ALIYUN_WORDART_TEXTURE) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ALIYUN)) //
                    .addHeader(ALIYUN_HEADER_DASH_SCOPE_ASYNC, ALIYUN_HEADER_DASH_SCOPE_ASYNC_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userToken, res, response);
        } catch (Exception e) {
            log.error("wordartTexture Aliyun ===================== exception:{}", e.getMessage());
        }
    }

    private void image2ImageSynthesis(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            String modelName = builder.getModelName();
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 模型名称
            paramMap.put("model", modelName);
            // 输入的基本信息，如提示词等
            JSONObject input = new JSONObject();
            // 正向提示词，用来描述生成图像中期望包含的元素和视觉特点。
            input.put("prompt", modelParam.getString("prompt"));
            //图像编辑功能。目前支持的功能有：
            // stylization_all：全局风格化，当前支持2种风格。风格和提示词技巧
            // stylization_local：局部风格化，当前支持8种风格。风格和提示词技巧
            // description_edit：指令编辑。通过指令即可编辑图像，简单编辑任务优先推荐这种方式。提示词技巧
            // description_edit_with_mask：局部重绘。需要指定编辑区域，适合对编辑范围有精确控制的场景。提示词技巧
            // remove_watermark：去文字水印。提示词技巧
            // expand：扩图。提示词技巧
            // super_resolution：图像超分。提示词技巧
            // colorization：图像上色。提示词技巧
            // doodle：线稿生图。提示词技巧
            // control_cartoon_feature：参考卡通形象生图。提示词技巧
            input.put("function", modelParam.getString("function"));
            // 输入图像的URL地址。
            input.put("base_image_url", modelParam.getString("base_image_url"));
            // 输入草图的URL地址。
            input.put("sketch_image_url", modelParam.getString("sketch_image_url"));
            // 仅当function设置为description_edit_with_mask（局部重绘）时必填，其余情况无需填写。
            input.put("mask_image_url", modelParam.getString("mask_image_url"));
            // 输入图像URL地址。
            input.put("image_url", modelParam.getString("image_url"));
            // 输入擦除区域掩码图像URL地址或者图像base64数据
            input.put("mask_url", modelParam.getString("mask_url"));
            // 输入保留区域掩码图像URL地址或者图像base64数据。
            input.put("foreground_url", modelParam.getString("foreground_url"));

            // 模特人物图片的公网URL。
            input.put("person_image_url", modelParam.getString("person_image_url"));
            // 上装/连衣裙服饰图的公网URL。
            input.put("top_garment_url", modelParam.getString("top_garment_url"));
            // 下装服饰图的公网URL。
            input.put("bottom_garment_url", modelParam.getString("bottom_garment_url"));

            paramMap.put("input", input);
            // 图像处理参数
            JSONObject parameters = new JSONObject();
            // 输出图像的分辨率。默认值是1024*1024
            parameters.put("size", modelParam.getString("size"));
            // 生成图片的数量。取值范围为1~4张，默认为4
            parameters.put("n", modelParam.getIntValue("n", 1));
            // 随机数种子，用于控制模型生成内容的随机性。seed参数取值范围是[0, 2147483647
            parameters.put("seed", modelParam.getInteger("seed"));
            // 去噪推理步数，一般步数越大，图像质量越高，步数越小，推理速度越快。 目前默认40，用户可以在1-500间进行调整。
            parameters.put("steps", modelParam.getIntValue("steps", 40));
            // 用于指导生成的结果与用户输入的prompt的贴合程度，越高则生成结果与用户输入的prompt更相近。目前默认4.5，倾向于输入4～5内的值。
            parameters.put("cfg", modelParam.getDoubleValue("cfg"));
            // 偏移量，用于调整生成内容的某些特性或参数。默认为3.0。
            parameters.put("shift", modelParam.getDoubleValue("shift"));
            // 是否开启prompt智能改写。开启后会使用大模型对输入prompt进行智能改写，仅对正向提示词有效。对于较短的输入prompt生成效果提升明显，但会增加3-4秒耗时
            parameters.put("prompt_extend", modelParam.getBooleanValue("prompt_extend"));
            // 是否添加水印标识，水印位于图片右下角，文案为“AI生成”。
            parameters.put("watermark", modelParam.getBooleanValue("watermark"));
            // 指导度量值，用于在图像生成过程中调整模型的创造性与文本指导的紧密度。较高的值会使得生成的图像更忠于文本提示，但可能减少多样性；较低的值则允许更多创造性，增加图像变化。默认值为3.5。
            parameters.put("guidance", modelParam.getDoubleValue("guidance"));
            // 一个布尔值，表示是否在采样过程中将部分计算密集型组件临时从GPU卸载到CPU，以减轻内存压力或提升效率。如果您的系统资源有限或希望加速采样过程，可以启用此选项，默认为False。
            parameters.put("offload", modelParam.getBooleanValue("offload"));
            // 当function设置为 stylization_all（全局风格化）时填写。 图像修改幅度。取值范围[0.0 1.0]，默认值为0.5。值越接近0，则越接近原图效果；值越接近1，对原图的修改幅度越大。
            parameters.put("strength", modelParam.getDoubleValue("strength"));
            // 当function设置为expand（扩图）时才需填写。 图像居中，向上按比例扩展图像。默认值为1.0，取值范围[1.0, 2.0]。
            parameters.put("top_scale", modelParam.getDoubleValue("top_scale"));
            // 当function设置为expand（扩图）时才需填写。 图像居中，向下按比例扩展图像。默认值为1.0，取值范围[1.0, 2.0]。
            parameters.put("bottom_scale", modelParam.getDoubleValue("bottom_scale"));
            // 当function设置为expand（扩图）时才需填写。 图像居中，向左按比例扩展图像。默认值为1.0，取值范围[1.0, 2.0]。
            parameters.put("left_scale", modelParam.getDoubleValue("left_scale"));
            // 当function设置为expand（扩图）时才需填写。 图像居中，向右按比例扩展图像。默认值为1.0，取值范围[1.0, 2.0]。
            parameters.put("right_scale", modelParam.getDoubleValue("right_scale"));
            // 当function设置为super_resolution（图像超分）时才需填写。 图像超分的放大倍数。在放大图像的同时增强细节，提升图像分辨率，实现高清处理。 取值范围为1~4，默认值为1。当upscale_factor设置为1时，仅对图像进行高清处理，不进行放大。
            parameters.put("upscale_factor", modelParam.getInteger("upscale_factor"));
            // 当function设置为doodle（线稿生图）时才需填写。 输入图像是否为线稿图像。
            // false：默认值，输入图像不为线稿图像。模型会先从输入图像中提取线稿，再参考提取的线稿生成新的图像。
            // true：输入图像为线稿图像。模型将直接基于输入图像生成图像，适用于涂鸦作画场景。
            parameters.put("is_sketch", modelParam.getBooleanValue("is_sketch"));
            // 输出图像的风格，目前支持以下风格取值：
            // <auto>：默认值，由模型随机输出图像风格。
            // <3d cartoon>：3D卡通。
            // <anime>：二次元。
            // <oil painting>：油画。
            // <watercolor>：水彩。
            // <sketch>：素描。
            // <chinese painting>：中国画。
            // <flat illustration>：扁平插画。
            parameters.put("style", modelParam.getString("style"));
            // 输入草图对输出图像的约束程度。取值范围为0-10，取值间隔为1， 默认值为10。取值越大表示输出图像跟输入草图越相似。
            parameters.put("sketch_weight", modelParam.getInteger("sketch_weight"));
            // 如果上传图片是RGB图片，而非草图（sketch线稿），此参数可控制是否对输入图片进行sketch边缘提取。 默认值为False，表示不进行提取
            parameters.put("sketch_extraction", modelParam.getBoolean("sketch_extraction"));
            // 此字段在sketch_extraction=false时生效，所包含数值均被视为画笔色，其余数值均会视为背景色。模型会基于一种或多种画笔色描绘的区域生成新的画作。默认值为[]。
            parameters.put("sketch_color", modelParam.get("sketch_color"));
            // RGB颜色数值列表，用于指定掩码图片中表示涂改区域的颜色。默认值为[]。
            parameters.put("mask_color", modelParam.get("mask_color"));
            // 是否为快速模式，默认为false，快速模式推理耗时约为非快速模式的四分之一，适合不需要生成大量细节的场景。
            parameters.put("fast_mode", modelParam.getBooleanValue("fast_mode"));
            // 默认为true，建议若擦除mask为算法分割结果，设置为true；若擦除mask为涂抹结果，设置为false。
            parameters.put("dilate_flag", modelParam.getBooleanValue("dilate_flag"));
            // 添加Generated by AI水印。默认值为true，在输出图像左下角处添加水印。
            parameters.put("add_watermark", modelParam.getBoolean("add_watermark"));

            // 输出图片的分辨率。-1：默认值，与原图尺寸保持一致。 1024：表示 576x1024 分辨率。 1280：表示 720x1280 分辨率。
            parameters.put("resolution", modelParam.getInteger("resolution"));
            // 是否还原模特图中的人脸。 true：默认值，保留原图人脸。 false：随机生成一张新的人脸。
            parameters.put("restore_face", modelParam.getBoolean("restore_face"));

            paramMap.put("parameters", parameters);
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("image2ImageSynthesis ============================ Aliyun request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ALIYUN) + ALIYUN_IMAGES_IMAGE_SYNTHESIS) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ALIYUN)) //
                    .addHeader(ALIYUN_HEADER_DASH_SCOPE_ASYNC, ALIYUN_HEADER_DASH_SCOPE_ASYNC_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userToken, res, response);
        } catch (Exception e) {
            log.error("image2ImageSynthesis Aliyun ===================== exception:{}", e.getMessage());
        }
    }

    private void imageGenerationSynthesis(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            String modelName = builder.getModelName();
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 模型名称
            paramMap.put("model", modelName);
            // 输入的基本信息，如提示词等
            JSONObject input = new JSONObject();
            // 输入的图像URL地址。
            input.put("image_url", modelParam.getString("image_url"));
            // 人像风格类型索引值。
            input.put("style_index", modelParam.getInteger("style_index"));
            // 风格参考图像URL地址。当style_index=-1时，必须传入，其他风格无需传入。
            input.put("style_ref_url", modelParam.getString("style_ref_url"));
            paramMap.put("input", input);
            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("imageGenerationSynthesis ============================ Aliyun request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ALIYUN) + ALIYUN_IMAGES_IMAGE_SYNTHESIS) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ALIYUN)) //
                    .addHeader(ALIYUN_HEADER_DASH_SCOPE_ASYNC, ALIYUN_HEADER_DASH_SCOPE_ASYNC_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userToken, res, response);
        } catch (Exception e) {
            log.error("imageGenerationSynthesis Aliyun ===================== exception:{}", e.getMessage());
        }
    }

    private void image2ImageOutPainting(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            String modelName = builder.getModelName();
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userToken = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 模型名称
            paramMap.put("model", modelName);
            // 输入的基本信息，如提示词等
            JSONObject input = new JSONObject();
            // 输入的图像URL地址。
            input.put("image_url", modelParam.getString("image_url"));
            paramMap.put("input", input);
            // 图像处理参数
            JSONObject parameters = new JSONObject();
            // 逆时针旋转角度。默认值为0，取值范围[0, 359]，单位为度。
            parameters.put("angle", modelParam.getInteger("angle"));
            // 图像宽高比。可选值有["","1:1", "3:4", "4:3", "9:16", "16:9"]。默认值为""，表示不设置输出图像的宽高比。
            parameters.put("output_ratio", modelParam.getString("output_ratio"));
            // 图像居中，在水平方向上按比例扩展图像。 默认值为1.0，取值范围[1.0, 3.0]。
            parameters.put("x_scale", modelParam.getDouble("x_scale"));
            // 图像居中，在垂直方向上按比例扩展图像。 默认值为1.0，取值范围[1.0, 3.0]。
            parameters.put("y_scale", modelParam.getDouble("y_scale"));
            // 在图像上方添加像素。 默认值为0，取值限制top_offset+bottom_offset < 3×输入图像高度。
            parameters.put("top_offset", modelParam.getInteger("top_offset"));
            // 在图像下方添加像素。 默认值为0，取值限制top_offset+bottom_offset < 3×输入图像高度。
            parameters.put("bottom_offset", modelParam.getInteger("bottom_offset"));
            // 在图像左侧添加像素。 默认值为0，扩展限制left_offset+right_offset < 3×输入图像宽度。
            parameters.put("left_offset", modelParam.getInteger("left_offset"));
            // 在图像右侧添加像素。 默认值为0，扩展限制left_offset+right_offset < 3×输入图像宽度。
            parameters.put("right_offset", modelParam.getInteger("right_offset"));
            // 开启图像最佳质量模式。默认值为false，减少图像生成的等待时间。
            parameters.put("best_quality", modelParam.getBoolean("best_quality"));
            // 限制模型生成的图像文件大小。默认值为true，当输入图像单边长度<=10000时，输出图像文件大小在5MB以下。
            parameters.put("limit_image_size", modelParam.getBoolean("limit_image_size"));
            // 添加Generated by AI水印。默认值为True，在输出图像左下角处添加水印。
            parameters.put("add_watermark", modelParam.getBoolean("add_watermark"));
            paramMap.put("parameters", parameters);

            String requestBody = JSONObject.toJSONString(paramMap);
            userToken.setRequestBody(requestBody);
            log.info("image2ImageOutPainting ============================ Aliyun request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ALIYUN) + ALIYUN_IMAGES_OUT_PAINTING) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ALIYUN)) //
                    .addHeader(ALIYUN_HEADER_DASH_SCOPE_ASYNC, ALIYUN_HEADER_DASH_SCOPE_ASYNC_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userToken, res, response);
        } catch (Exception e) {
            log.error("image2ImageOutPainting Aliyun ===================== exception:{}", e.getMessage());
        }
    }

    /**
     * <a href="https://help.aliyun.com/zh/model-studio/image-to-video-api-reference/?spm=a2c4g.11186623.help-menu-2400256.d_2_3_0.111a4a3eqQlcLM&scm=20140722.H_2867393._.OR_help-T_cn~zh-V_1">...</a>
     */
    @Override
    public void video(CommonModelBuilder builder, HttpServletResponse response) {
        String modelFunction = builder.getUserModelUsage().getModelFunction();
        if (StringUtils.containsIgnoreCase(modelFunction, TEXT_TO_VIDEO.getValue()) //
                || StringUtils.containsIgnoreCase(modelFunction, IMAGE_TO_VIDEO.getValue())) {
            videoGenerationSynthesis(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, MULTI_IMAGE_TO_VIDEO.getValue())) {
            image2VideoSynthesis(builder, response);
        }
    }

    private void videoGenerationSynthesis(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            String modelName = builder.getModelName();
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userModelUsage = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 模型名称
            paramMap.put("model", modelName);
            // 输入的基本信息
            JSONObject input = new JSONObject();
            // 文本提示词。支持中英文，长度不超过800个字符，每个汉字/字母占一个字符，超过部分会自动截断。 当使用视频特效参数（即template不为空）时，prompt参数无效，无需填写。
            input.put("prompt", modelParam.getString("prompt"));
            // 反向提示词，用来描述不希望在视频画面中看到的内容，可以对视频画面进行限制。
            input.put("negative_prompt", modelParam.getString("negative_prompt"));
            // 首帧图像的URL或 Base64 编码数据。
            input.put("img_url", modelParam.getString("img_url"));
            // 指定视频特效模板的名称。若未填写，则表示不使用任何视频特效。 当前支持的模板：squish（解压捏捏）、flying（魔法悬浮）、carousel（时光木马）等
            input.put("template", modelParam.getString("template"));
            // 功能名称。多图参考设置为image_reference。
            // 功能名称。视频重绘设置为 video_repainting。
            // 功能名称。局部编辑设置为 video_edit。
            // 功能名称。视频延展设置为 video_extension。
            // 功能名称。视频画面扩展设置为 video_outpainting。
            input.put("function", modelParam.getString("function"));
            // 输入参考图像的URL 数组。
            input.put("ref_images_url", modelParam.get("ref_images_url"));
            // 输入视频的URL地址。
            input.put("video_url", modelParam.getString("video_url"));
            // 掩码图像的URL地址
            input.put("mask_image_url", modelParam.getString("mask_image_url"));
            // 当 mask_image_url 不为空时，该参数生效，用于标识掩码目标出现在视频中的哪一帧，以“帧 ID”表示。 默认值为 1，单位为帧，表示视频的第一帧（首帧）。
            input.put("mask_frame_id", modelParam.getInteger("mask_frame_id"));
            // 掩码视频的URL地址。
            input.put("mask_video_url", modelParam.getString("mask_video_url"));
            // 首帧图像的URL地址。
            input.put("first_frame_url", modelParam.getString("first_frame_url"));
            // 尾帧图像的URL地址
            input.put("last_frame_url", modelParam.getString("last_frame_url"));
            // 首段视频的URL地址。
            input.put("first_clip_url", modelParam.getString("first_clip_url"));
            // 尾段视频的URL地址。
            input.put("last_clip_url", modelParam.getString("last_clip_url"));
            paramMap.put("input", input);
            // 图像处理参数
            JSONObject parameters = new JSONObject();
            // 生成视频的分辨率档位。不同模型支持的分辨档位如下：480P、720P、1080P
            parameters.put("resolution", modelParam.getString("resolution"));
            // 用于指定视频分辨率，格式为宽*高。不同模型支持的分辨率如下
            // wan2.2-t2v-plus：支持480P和1080P对应的所有分辨率。默认分辨率为1920*1080（1080P）。
            // wanx2.1-t2v-turbo：支持 480P 和 720P 对应的所有分辨率。默认分辨率为1280*720（720P）。
            // wanx2.1-t2v-plus：仅支持 720P 对应的所有分辨率。默认分辨率为1280*720（720P）。
            parameters.put("size", modelParam.getString("size"));
            // wanx2.1-vace-plus 通义万相-视频编辑统一模型
            // 该参数用于标识每张参考图像的用途，与 ref_images_url 参数一一对应。数组中每个元素表示对应位置的图像为“主体”还是“背景”：obj：表示该图像作为主体参考。 bg：表示该图像作为背景参考 （最多仅允许一个）。
            parameters.put("obj_or_bg", modelParam.get("obj_or_bg"));
            // 设置视频特征提取的方式。
            // posebodyface：提取输入视频中主体的脸部表情和肢体动作，适用于需保留主体表情细节的场景。
            // posebody：提取输入视频中主体的肢体动作（不含脸部表情），适用于只需要控制主体身体动作的场景。
            // depth：提取输入视频的构图和运动轮廓。
            // scribble：提取输入视频的线稿结构。
            parameters.put("control_condition", modelParam.getString("control_condition"));
            // 调节 control_condition 所指定的视频特征提取方式对生成视频的控制强度。 默认值为1.0，取值范围[0.0, 1.0]。
            parameters.put("strength", modelParam.getDouble("strength"));
            // 当 mask_image_url 不为空时，该参数生效，用于指定编辑区域的行为方式。
            parameters.put("mask_type", modelParam.getString("mask_type"));
            // 当 mask_type 为 tracking 时，该参数生效，表示对掩码区域进行向外扩展的比例。
            parameters.put("expand_ratio", modelParam.getDouble("expand_ratio"));
            // 当 mask_type 为 tracking 时，该参数生效，表示掩码区域的形状。
            parameters.put("expand_mode", modelParam.getString("expand_mode"));
            // 生成视频的时长，默认值为5，单位为秒。
            parameters.put("duration", modelParam.getIntValue("duration", 5));
            // 是否开启prompt智能改写。开启后使用大模型对输入prompt进行智能改写。对于较短的prompt生成效果提升明显，但会增加耗时。true：默认值，开启智能改写。
            parameters.put("prompt_extend", modelParam.getBooleanValue("prompt_extend", true));
            // 随机数种子，用于控制模型生成内容的随机性。取值范围为[0, 2147483647]
            parameters.put("seed", modelParam.getInteger("seed"));
            // 是否添加水印标识，水印位于图片右下角，文案为“AI生成”。 false：默认值，不添加水印。
            parameters.put("watermark", modelParam.getBooleanValue("watermark", false));
            paramMap.put("parameters", parameters);
            String requestBody = JSONObject.toJSONString(paramMap);
            userModelUsage.setRequestBody(requestBody);
            log.info("videoGenerationSynthesis Aliyun ============================  request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ALIYUN) + ALIYUN_VIDEO_SYNTHESIS) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ALIYUN)) //
                    .addHeader(ALIYUN_HEADER_DASH_SCOPE_ASYNC, ALIYUN_HEADER_DASH_SCOPE_ASYNC_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealVideoTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userModelUsage, res, response);
        } catch (Exception e) {
            log.error("videoGenerationSynthesis Aliyun ===================== exception:{}", e.getMessage());
        }
    }

    public void image2VideoSynthesis(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            String modelName = builder.getModelName();
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userModelUsage = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 模型名称
            paramMap.put("model", modelName);
            // 输入的基本信息
            JSONObject input = new JSONObject();
            // 用户上传的图片 URL，该图应先通过EMO图像检测API
            input.put("image_url", modelParam.getString("image_url"));
            // 用户上传的音频文件 URL， 用于EMO模型推理的输入。
            input.put("audio_url", modelParam.getString("video_url"));
            // 图片中人脸区域bbox，应输入EMO图像检测API出参中同名字段的值。
            input.put("face_bbox", modelParam.get("face_bbox"));
            // 图片中动态区域bbox，应输入EMO图像检测API出参中同名字段的值。
            input.put("ext_bbox", modelParam.get("ext_bbox"));
            // 文本提示词。支持中英文，长度不超过800个字符，每个汉字/字母占一个字符，超过部分会自动截断。如果首尾帧的主体和场景变化较大，建议描写变化过程，例如运镜过程（镜头向左移动）、或者主体运动过程（人向前奔跑）。
            input.put("prompt", modelParam.getString("prompt"));
            // 反向提示词，用来描述不希望在视频画面中看到的内容，可以对视频画面进行限制。 支持中英文，长度不超过500个字符，超过部分会自动截断。
            input.put("negative_prompt", modelParam.getString("negative_prompt"));
            // 首帧图像的URL或 Base64 编码数据。
            input.put("first_frame_url", modelParam.getString("first_frame_url"));
            // 尾帧图像的URL或 Base64 编码数据。
            input.put("last_frame_url", modelParam.getString("last_frame_url"));
            paramMap.put("input", input);
            // 视频处理参数
            JSONObject parameters = new JSONObject();
            // 可选择动作风格强度控制人物的运动姿态和幅度，当前支持3种：normal、calm、active，分别对应人物动作风格适中、平静、活泼。默认为normal。
            parameters.put("style_level", modelParam.getString("style_level"));
            // 生成视频的分辨率档位。默认值为720P，当前仅支持720P。
            parameters.put("resolution", modelParam.getString("resolution"));
            // 视频生成时长，单位为秒。当前参数值固定为5，且不支持修改。模型将始终生成5秒时长的视频。
            parameters.put("duration", modelParam.getIntValue("duration", 5));
            // 是否开启prompt智能改写。开启后使用大模型对输入prompt进行智能改写。对于较短的prompt生成效果提升明显，但会增加耗时。true：默认值，开启智能改写。
            parameters.put("prompt_extend", modelParam.getBooleanValue("prompt_extend", true));
            // 随机数种子，用于控制模型生成内容的随机性。取值范围为[0, 2147483647]
            parameters.put("seed", modelParam.getInteger("seed"));
            // 是否添加水印标识，水印位于图片右下角，文案为“AI生成”。 false：默认值，不添加水印。
            parameters.put("watermark", modelParam.getBooleanValue("watermark", false));
            paramMap.put("parameters", parameters);
            String requestBody = JSONObject.toJSONString(paramMap);
            userModelUsage.setRequestBody(requestBody);
            log.info("image2VideoSynthesis Aliyun ============================  request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ALIYUN) + ALIYUN_IMAGE2_VIDEO_SYNTHESIS) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ALIYUN)) //
                    .addHeader(ALIYUN_HEADER_DASH_SCOPE_ASYNC, ALIYUN_HEADER_DASH_SCOPE_ASYNC_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealVideoTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userModelUsage, res, response);
        } catch (Exception e) {
            log.error("image2VideoSynthesis Aliyun ===================== exception:{}", e.getMessage());
        }
    }


    @Override
    public void audio(CommonModelBuilder builder, HttpServletResponse response) {
        String modelFunction = builder.getUserModelUsage().getModelFunction();
        if (StringUtils.containsIgnoreCase(modelFunction, AUDIO_SPEECH.getValue())) {
            audioGeneration(builder, response);
        } else if (StringUtils.containsIgnoreCase(modelFunction, AUDIO_TRANSCRIPTION.getValue())) {
            audioTranscription(builder, response);
        }
    }

    private void audioGeneration(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            String modelName = builder.getModelName();
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userModelUsage = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 模型名称
            paramMap.put("model", modelName);
            // 输入的基本信息
            JSONObject input = new JSONObject();
            // 要合成的文本，支持中文、英文、中英混合输入。最长输入为512 Token。
            input.put("text", modelParam.getString("text"));
            // 使用的音色，可选值：Cherry、Serena、Ethan、Chelsie、Dylan、Jada、Sunny
            input.put("voice", modelParam.getString("voice"));
            paramMap.put("input", input);
            String requestBody = JSONObject.toJSONString(paramMap);
            userModelUsage.setRequestBody(requestBody);
            log.info("audioGeneration Aliyun ============================  request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ALIYUN) + ALIYUN_AUDIO_GENERATION) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ALIYUN)) //
                    .build();

            Response res = client.newCall(request).execute();
            dealPostTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userModelUsage, res, response);
        } catch (Exception e) {
            log.error("audioGeneration Aliyun ===================== exception:{}", e.getMessage());
        }
    }

    private void audioTranscription(CommonModelBuilder builder, HttpServletResponse response) {
        try {
            String modelName = builder.getModelName();
            JSONObject modelParam = builder.getModelParam();
            UserModelUsage userModelUsage = builder.getUserModelUsage();
            Map<String, Object> paramMap = MapUtils.newHashMap();
            // 模型名称
            paramMap.put("model", modelName);
            // 输入的基本信息
            JSONObject input = new JSONObject();
            // array[string] 音视频文件转写的URL列表，支持HTTP / HTTPS协议，单次请求最多支持100个URL。"file_urls": ["url1","url2]
            input.put("file_urls", modelParam.get("file_urls"));
            paramMap.put("input", input);
            // 图像处理参数
            JSONObject parameters = new JSONObject();
            // 最新热词ID，支持最新v2系列模型并配置语种信息，此次语音识别中生效此热词ID对应的热词信息。默认不启用
            parameters.put("vocabulary_id", modelParam.getString("vocabulary_id"));
            // 指定在多音轨文件中需要进行语音识别的音轨索引，以List的形式给出，例如[0]表示仅识别第一条音轨，[0, 1]表示同时识别前两条音轨。
            parameters.put("channel_id", modelParam.get("channel_id"));
            // 过滤语气词，默认关闭
            parameters.put("disfluency_removal_enabled", modelParam.getBooleanValue("disfluency_removal_enabled"));
            // 是否启用时间戳校准功能，默认关闭。
            parameters.put("timestamp_alignment_enabled", modelParam.getBooleanValue("timestamp_alignment_enabled"));
            // 指定在语音识别过程中需要处理的敏感词，并支持对不同敏感词设置不同的处理方式。
            parameters.put("special_word_filter", modelParam.getString("special_word_filter"));
            // 指定待识别语音的语言代码。 ["zh", "en"] 支持的语言代码： zh: 中文 en: 英文 ja: 日语 yue: 粤语 ko: 韩语 de：德语 fr：法语 ru：俄语
            parameters.put("language_hints", modelParam.get("language_hints"));
            // 自动说话人分离，默认关闭。
            parameters.put("diarization_enabled", modelParam.getBooleanValue("diarization_enabled"));
            // 说话人数量参考值。取值范围为2至100的整数（包含2和100）。
            parameters.put("speaker_count", modelParam.getInteger("speaker_count"));
            paramMap.put("parameters", parameters);
            String requestBody = JSONObject.toJSONString(paramMap);
            userModelUsage.setRequestBody(requestBody);
            log.info("audioTranscription Aliyun ============================  request:{}", requestBody);
            RequestBody body = RequestBody.create(requestBody, MEDIA_TYPE_JSON);

            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(3, TimeUnit.MINUTES) // 3分
                    .readTimeout(5, TimeUnit.MINUTES) // 5分
                    .writeTimeout(5, TimeUnit.MINUTES) // 5分
                    .build();

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(builder.getBaseUrl(), API_URL_ALIYUN) + ALIYUN_AUDIO_ASR_TRANSCRIPTION) //
                    .post(body) //
                    .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(builder.getApiKey(), API_KEY_ALIYUN)) //
                    .addHeader(ALIYUN_HEADER_DASH_SCOPE_ASYNC, ALIYUN_HEADER_DASH_SCOPE_ASYNC_VALUE) //
                    .build();

            Response res = client.newCall(request).execute();
            dealPostTaskResponse(builder.getBaseUrl(), builder.getApiKey(), userModelUsage, res, response);
        } catch (Exception e) {
            log.error("audioTranscription Aliyun ===================== exception:{}", e.getMessage());
        }
    }


    /**
     * 处理返回结果
     */
    private void dealVideoTaskResponse(String baseUrl, String apiKey, UserModelUsage userToken, Response res, HttpServletResponse response) {
        try {
            if (res.isSuccessful()) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("dealVideoTaskResponse Aliyun ====================== body:{}", bodyStr);
                JSONObject bodyObject = JSONObject.parseObject(bodyStr);
                String taskId = bodyObject.getJSONObject("output").getString("task_id");
                JSONObject taskResult;
                JSONObject outputObject;
                for (int i = 0; i < 60; i++) {
                    ThreadUtil.sleep(3000);
                    taskResult = getTask(baseUrl, apiKey, taskId);
                    if (null == taskResult) {
                        break;
                    }
                    outputObject = taskResult.getJSONObject("output");
                    if ("PENDING".equalsIgnoreCase(outputObject.getString("task_status")) //
                            || "RUNNING".equalsIgnoreCase(outputObject.getString("task_status"))) {
                        ThreadUtil.sleep(5000);
                        continue;
                    }
                    if ("SUCCEEDED".equalsIgnoreCase(outputObject.getString("task_status"))) {
                        String videoUrl = outputObject.getString("video_url");
                        Date end = new Date();
                        long costTime = end.getTime() - userToken.getRequestTime().getTime();
                        userToken.setResponseTime(end);
                        userToken.setDurationSeconds(NumberUtils.div(String.valueOf(costTime), "1000", 2));
                        userToken.setTotalDuration(taskResult.getJSONObject("usage").getIntValue("duration", 0));
                        userToken.setResponseBody(taskResult.toJSONString());
                        DisruptorData disruptorData = new DisruptorData();
                        disruptorData.setServiceName(USER_VIDEO_SERVICE);
                        disruptorData.setData(userToken);
                        disruptorProducer.pushData(disruptorData);

                        PrintWriter writer = response.getWriter();
                        writer.write(JSON.toJSONString(R.success(videoUrl)));
                        writer.flush();
                        break;
                    }
                    PrintWriter writer = response.getWriter();
                    writer.write(JSON.toJSONString(R.error(outputObject.getString("message"))));
                    writer.flush();
                    break;
                }
            } else {
                response.setStatus(res.code());
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(R.error(new String(res.body().bytes(), StandardCharsets.UTF_8))));
                writer.flush();
                log.error("dealVideoTaskResponse Aliyun ================= code:{},response:{}", res.code(), res.message());
            }
        } catch (Exception e) {
            log.error("dealVideoTaskResponse Aliyun ===================== exception:{}", e.getMessage());
        }
    }


    /**
     * 处理返回结果
     */
    private void dealTaskResponse(String baseUrl, String apiKey, UserModelUsage userToken, Response res, HttpServletResponse response) {
        try {
            if (res.isSuccessful()) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("dealTaskResponse Aliyun ====================== body:{}", bodyStr);
                JSONObject bodyObject = JSONObject.parseObject(bodyStr);
                String taskId = bodyObject.getJSONObject("output").getString("task_id");
                JSONObject taskResult;
                JSONObject outputObject;
                for (int i = 0; i < 60; i++) {
                    ThreadUtil.sleep(3000);
                    taskResult = getTask(baseUrl, apiKey, taskId);
                    if (null == taskResult) {
                        break;
                    }
                    outputObject = taskResult.getJSONObject("output");
                    if ("PENDING".equalsIgnoreCase(outputObject.getString("task_status")) //
                            || "RUNNING".equalsIgnoreCase(outputObject.getString("task_status"))) {
                        ThreadUtil.sleep(5000);
                        continue;
                    }
                    if ("SUCCEEDED".equalsIgnoreCase(outputObject.getString("task_status"))) {
                        Date end = new Date();
                        long costTime = end.getTime() - userToken.getRequestTime().getTime();
                        userToken.setResponseTime(end);
                        userToken.setDurationSeconds(NumberUtils.div(String.valueOf(costTime), "1000", 2));
                        userToken.setTotalCount(taskResult.getJSONObject("usage").getIntValue("image_count", 0));
                        userToken.setResponseBody(taskResult.toJSONString());
                        DisruptorData disruptorData = new DisruptorData();
                        disruptorData.setServiceName(USER_IMAGE_SERVICE);
                        disruptorData.setData(userToken);
                        disruptorProducer.pushData(disruptorData);

                        List<String> imageUrls = CollectionUtils.newArrayList();
                        if (outputObject.containsKey("output_image_url")) {
                            imageUrls.add(outputObject.getString("output_image_url"));
                        } else if (outputObject.containsKey("output_vis_image_url")) {
                            imageUrls.add(outputObject.getString("output_vis_image_url"));
                        } else if (outputObject.containsKey("render_urls")) {
                            imageUrls.add(outputObject.getString("render_urls"));
                        } else if (outputObject.containsKey("bg_urls")) {
                            imageUrls.add(outputObject.getString("bg_urls"));
                        } else if (outputObject.containsKey("result_url")) {
                            imageUrls.add(outputObject.getString("result_url"));
                        } else if (outputObject.containsKey("results")) {
                            JSONArray results = outputObject.getJSONArray("results");
                            for (int j = 0; j < results.size(); j++) {
                                JSONObject result = results.getJSONObject(j);
                                if (result.containsKey("url")) {
                                    imageUrls.add(result.getString("url"));
                                } else if (result.containsKey("png_url")) {
                                    imageUrls.add(result.getString("png_url"));
                                }
                            }
                        }
                        PrintWriter writer = response.getWriter();
                        writer.write(JSON.toJSONString(R.success(imageUrls)));
                        writer.flush();
                        break;
                    }
                    PrintWriter writer = response.getWriter();
                    writer.write(JSON.toJSONString(R.error(outputObject.getString("message"))));
                    writer.flush();
                    break;
                }
            } else {
                response.setStatus(res.code());
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(R.error(new String(res.body().bytes(), StandardCharsets.UTF_8))));
                writer.flush();
                log.error("dealTaskResponse Aliyun ================= code:{},response:{}", res.code(), res.message());
            }
        } catch (Exception e) {
            log.error("dealTaskResponse Aliyun ===================== exception:{}", e.getMessage());
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
                    .url(StringUtils.blankToDefault(baseUrl, API_URL_ALIYUN) + ALIYUN_TASKS + taskId) //
                    .get() //
                    .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(apiKey, API_KEY_ALIYUN)) //
                    .build();

            Response res = client.newCall(request).execute();
            if (res.isSuccessful()) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("getTask Aliyun ====================== data:{}", bodyStr);
                return JSONObject.parseObject(bodyStr);
            }
        } catch (Exception e) {
            log.error("getTask Aliyun ===================== exception:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 处理返回结果
     */
    private void dealPostTaskResponse(String baseUrl, String apiKey, UserModelUsage userToken, Response res, HttpServletResponse response) {
        try {
            if (res.isSuccessful()) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("dealPostTaskResponse Aliyun ====================== body:{}", bodyStr);
                JSONObject bodyObject = JSONObject.parseObject(bodyStr);
                String taskId = bodyObject.getJSONObject("output").getString("task_id");
                JSONObject taskResult;
                JSONObject outputObject;
                for (int i = 0; i < 60; i++) {
                    ThreadUtil.sleep(3000);
                    taskResult = postTask(baseUrl, apiKey, taskId);
                    if (null == taskResult) {
                        break;
                    }
                    outputObject = taskResult.getJSONObject("output");
                    if ("PENDING".equalsIgnoreCase(outputObject.getString("task_status")) //
                            || "RUNNING".equalsIgnoreCase(outputObject.getString("task_status"))) {
                        ThreadUtil.sleep(5000);
                        continue;
                    }
                    if ("SUCCEEDED".equalsIgnoreCase(outputObject.getString("task_status"))) {
                        List<String> urls = CollectionUtils.newArrayList();
                        JSONArray results = outputObject.getJSONArray("results");
                        for (int j = 0; j < results.size(); j++) {
                            JSONObject result = results.getJSONObject(j);
                            if ("SUCCEEDED".equalsIgnoreCase(result.getString("subtask_status")) && result.containsKey("transcription_url")) {
                                urls.add(result.getString("transcription_url"));
                            }
                        }
                        Date end = new Date();
                        long costTime = end.getTime() - userToken.getRequestTime().getTime();
                        userToken.setResponseTime(end);
                        userToken.setDurationSeconds(NumberUtils.div(String.valueOf(costTime), "1000", 2));
                        userToken.setTotalCount(urls.size());
                        userToken.setResponseBody(taskResult.toJSONString());
                        DisruptorData disruptorData = new DisruptorData();
                        disruptorData.setServiceName(USER_IMAGE_SERVICE);
                        disruptorData.setData(userToken);
                        disruptorProducer.pushData(disruptorData);

                        PrintWriter writer = response.getWriter();
                        writer.write(JSON.toJSONString(R.success(urls)));
                        writer.flush();
                        break;
                    }
                    PrintWriter writer = response.getWriter();
                    writer.write(JSON.toJSONString(R.error("音频文件识别失败")));
                    writer.flush();
                    break;
                }
            } else {
                response.setStatus(res.code());
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(R.error(new String(res.body().bytes(), StandardCharsets.UTF_8))));
                writer.flush();
                log.error("dealPostTaskResponse Aliyun ================= code:{},response:{}", res.code(), res.message());
            }
        } catch (Exception e) {
            log.error("dealPostTaskResponse Aliyun ===================== exception:{}", e.getMessage());
        }
    }

    private JSONObject postTask(String baseUrl, String apiKey, String taskId) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder() //
                    .connectTimeout(20, TimeUnit.SECONDS) // 20秒
                    .readTimeout(30, TimeUnit.SECONDS) // 30秒
                    .writeTimeout(30, TimeUnit.SECONDS) // 30秒
                    .build();

            RequestBody body = RequestBody.create(new JSONObject().toJSONString(), MEDIA_TYPE_JSON);

            Request request = new Request.Builder() //
                    .url(StringUtils.blankToDefault(baseUrl, API_URL_ALIYUN) + ALIYUN_TASKS + taskId) //
                    .post(body) //
                    .addHeader(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE) //
                    .addHeader(HttpHeaders.AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + StringUtils.blankToDefault(apiKey, API_KEY_ALIYUN)) //
                    .build();

            Response res = client.newCall(request).execute();
            if (res.isSuccessful()) {
                String bodyStr = new String(res.body().bytes(), StandardCharsets.UTF_8);
                log.info("postTask Aliyun ====================== data:{}", bodyStr);
                return JSONObject.parseObject(bodyStr);
            }
        } catch (Exception e) {
            log.error("postTask Aliyun ===================== exception:{}", e.getMessage());
        }
        return null;
    }

}
