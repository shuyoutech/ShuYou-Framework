package com.shuyoutech.aigc.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.aigc.constant.AiConstants;
import com.shuyoutech.aigc.domain.model.TokenUsage;
import com.shuyoutech.aigc.domain.model.UserModelUsage;
import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import com.shuyoutech.common.core.util.NumberUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.disruptor.model.DisruptorData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import static com.shuyoutech.aigc.constant.AiConstants.*;
import static com.shuyoutech.common.disruptor.init.DisruptorRunner.disruptorProducer;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author YangChao
 * @date 2025-07-15 13:57
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class SSEChatEventListener extends EventSourceListener {

    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private UserModelUsage userModelUsage;
    private HttpServletResponse response;
    private StringBuilder contentSb = new StringBuilder();
    private StringBuilder dataSb = new StringBuilder();
    private TokenUsage tokenUsage;
    private String provider;

    public SSEChatEventListener(UserModelUsage userModelUsage, HttpServletResponse response) {
        this.userModelUsage = userModelUsage;
        this.response = response;
        this.provider = userModelUsage.getProvider();
        this.tokenUsage = TokenUsage.builder().inputTokenCount(0).outputTokenCount(0).totalTokenCount(0).build();
    }

    @Override
    public void onClosed(@NotNull EventSource eventSource) {
        try {
            // 设置消耗时间
            Date end = new Date();
            long costTime = end.getTime() - userModelUsage.getRequestTime().getTime();
            userModelUsage.setResponseTime(end);
            userModelUsage.setDurationSeconds(NumberUtils.div(String.valueOf(costTime), "1000", 2));
            if (null != tokenUsage) {
                userModelUsage.setInputTokenCount(tokenUsage.getInputTokenCount());
                userModelUsage.setOutputTokenCount(tokenUsage.getOutputTokenCount());
                userModelUsage.setTotalTokenCount(tokenUsage.getTotalTokenCount());
            }
            userModelUsage.setAssistantMessage(contentSb.toString());
            userModelUsage.setResponseBody(dataSb.toString());
            DisruptorData disruptorData = new DisruptorData();
            disruptorData.setServiceName(AiConstants.USER_CHAT_SERVICE);
            disruptorData.setData(userModelUsage);
            disruptorProducer.pushData(disruptorData);

            JSONObject json = new JSONObject();
            json.put("conversationId", userModelUsage.getConversationId());
            json.put("inputTokenCount", userModelUsage.getInputTokenCount());
            json.put("outputTokenCount", userModelUsage.getOutputTokenCount());
            json.put("durationSeconds", userModelUsage.getDurationSeconds());
            json.put("totalLength", contentSb.length());
            response.getWriter().write("event:" + EVENT_END);
            response.getWriter().println();
            response.getWriter().write("data: " + json.toJSONString());
            response.getWriter().println();
            response.getWriter().println();
            response.getWriter().flush();

            countDownLatch.countDown();
        } catch (Exception e) {
            log.error("onClosed ====================== exception:{}", e.getMessage());
        }
    }

    @Override
    public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
        try {
            dataSb.append(data).append("\n");
            if (!JSON.isValidObject(data)) {
                return;
            }
            JSONObject object = JSONObject.parseObject(data);
            if (AiProviderTypeEnum.GOOGLE.getValue().equals(provider)) {
                JSONArray candidates = object.getJSONArray("candidates");
                JSONObject usage = object.getJSONObject("usageMetadata");
                if (null != usage) {
                    tokenUsage.setInputTokenCount(usage.getIntValue("promptTokenCount", 0));
                    tokenUsage.setOutputTokenCount(usage.getIntValue("candidatesTokenCount", 0));
                    tokenUsage.setTotalTokenCount(usage.getIntValue("totalTokenCount", 0));
                }
                if (null != candidates && !candidates.isEmpty()) {
                    JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                    if (null != content && null != content.getJSONArray("parts") && !content.getJSONArray("parts").isEmpty()) {
                        JSONArray parts = content.getJSONArray("parts");
                        JSONObject part = parts.getJSONObject(0);
                        contentSb.append(part.getString("text"));
                        JSONObject json = new JSONObject();
                        json.put("content", part.getString("text"));
                        response.getWriter().write("event:" + EVENT_ANSWER);
                        response.getWriter().println();
                        response.getWriter().write("data: " + json.toJSONString());
                        response.getWriter().println();
                        response.getWriter().println();
                        response.getWriter().flush();
                    }
                }
            } else if (AiProviderTypeEnum.ANTHROPIC.getValue().equals(provider)) {
                JSONObject delta = object.getJSONObject("delta");
                JSONObject usage = object.getJSONObject("usage");
                if (null != usage) {
                    if (usage.containsKey("input_tokens")) {
                        tokenUsage.setInputTokenCount(usage.getIntValue("input_tokens", 0) + tokenUsage.getInputTokenCount());
                    }
                    if (usage.containsKey("output_tokens")) {
                        tokenUsage.setOutputTokenCount(usage.getIntValue("output_tokens", 0) + tokenUsage.getOutputTokenCount());
                    }
                    tokenUsage.setTotalTokenCount(tokenUsage.getInputTokenCount() + tokenUsage.getOutputTokenCount());
                }
                if (null != delta) {
                    String content = null;
                    if (StringUtils.isNotBlank(delta.getString("text"))) {
                        content = delta.getString("text");
                    } else if (StringUtils.isNotBlank(delta.getString("thinking"))) {
                        content = delta.getString("thinking");
                    } else if (StringUtils.isNotBlank(delta.getString("partial_json"))) {
                        content = delta.getString("partial_json");
                    }
                    if (StringUtils.isNotBlank(content)) {
                        contentSb.append(content);
                        JSONObject json = new JSONObject();
                        json.put("content", content);
                        response.getWriter().write("event:" + EVENT_ANSWER);
                        response.getWriter().println();
                        response.getWriter().write("data: " + json.toJSONString());
                        response.getWriter().println();
                        response.getWriter().println();
                        response.getWriter().flush();
                    }
                }
            } else {
                JSONArray choices = object.getJSONArray("choices");
                JSONObject usage = object.getJSONObject("usage");
                if (null != usage) {
                    if (usage.containsKey("prompt_tokens")) {
                        tokenUsage.setInputTokenCount(usage.getIntValue("prompt_tokens", 0));
                    } else if (usage.containsKey("input_tokens")) {
                        tokenUsage.setInputTokenCount(usage.getIntValue("input_tokens", 0));
                    }
                    if (usage.containsKey("completion_tokens")) {
                        tokenUsage.setOutputTokenCount(usage.getIntValue("completion_tokens", 0));
                    } else if (usage.containsKey("output_tokens")) {
                        tokenUsage.setOutputTokenCount(usage.getIntValue("output_tokens", 0));
                    }
                    if (usage.containsKey("total_tokens")) {
                        tokenUsage.setTotalTokenCount(usage.getIntValue("total_tokens", 0));
                    } else {
                        tokenUsage.setTotalTokenCount(tokenUsage.getInputTokenCount() + tokenUsage.getOutputTokenCount());
                    }
                }
                if (null != choices && !choices.isEmpty()) {
                    JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
                    if (null != delta) {
                        String content = delta.getString("content");
                        if (StringUtils.isNotBlank(content)) {
                            contentSb.append(content);
                            JSONObject json = new JSONObject();
                            json.put("content", content);
                            response.getWriter().write("event:" + EVENT_ANSWER);
                            response.getWriter().println();
                            response.getWriter().write("data: " + json.toJSONString());
                            response.getWriter().println();
                            response.getWriter().println();
                            response.getWriter().flush();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("onEvent ====================== exception:{}", e.getMessage());
        }
    }

    @Override
    public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response res) {
        if (null != res) {
            try {
                JSONObject object = new JSONObject();
                object.put("code", res.code());
                object.put("message", res.message());
                response.setContentType(APPLICATION_JSON_VALUE);
                response.setStatus(res.code());
                String body = new String(res.body().bytes(), StandardCharsets.UTF_8);
                response.getWriter().write(body);
                object.put("body", body);
                response.getWriter().flush();
                dataSb.append(object.toJSONString());
            } catch (Exception e) {
                log.error("onFailure ====================== exception:{}", e.getMessage());
            }
        }
        countDownLatch.countDown();
    }

    @Override
    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
    }

}
