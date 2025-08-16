package com.shuyoutech.aigc.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.aigc.domain.bo.ChatModelBo;
import com.shuyoutech.aigc.domain.bo.CommonModelBo;
import com.shuyoutech.aigc.domain.entity.AigcChatConversationEntity;
import com.shuyoutech.aigc.domain.entity.AigcChatMessageEntity;
import com.shuyoutech.aigc.domain.entity.AigcModelEntity;
import com.shuyoutech.aigc.domain.model.ChatMessage;
import com.shuyoutech.aigc.domain.model.ChatModelBuilder;
import com.shuyoutech.aigc.domain.model.CommonModelBuilder;
import com.shuyoutech.aigc.domain.model.UserModelUsage;
import com.shuyoutech.aigc.provider.AigcModelFactory;
import com.shuyoutech.aigc.provider.service.ModelService;
import com.shuyoutech.aigc.provider.service.impl.OpenRouterService;
import com.shuyoutech.api.model.LoginUser;
import com.shuyoutech.common.core.util.BooleanUtils;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.SpringUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.satoken.util.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.List;

import static com.shuyoutech.aigc.constant.AiConstants.ROLE_ASSISTANT;
import static com.shuyoutech.aigc.constant.AiConstants.ROLE_USER;
import static com.shuyoutech.common.core.constant.CommonConstants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * @author YangChao
 * @date 2025-07-13 15:50
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AigcServiceImpl implements AigcService {

    @Override
    public void chat(ChatModelBo bo, HttpServletResponse response) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(TEXT_EVENT_STREAM_VALUE);
            response.setCharacterEncoding(CHARSET_UTF_8);
            response.setHeader(CACHE_CONTROL, NO_CACHE);

            String provider = bo.getProvider();
            String modelName = bo.getModel();
            JSONObject modelParam = bo.getModelParam();
            String conversationId = StringUtils.isEmpty(bo.getConversationId()) ? IdUtil.simpleUUID() : bo.getConversationId();
            String loginUserId = AuthUtils.getLoginUserId();
            String loginUserName = AuthUtils.getLoginUserName();
            String userMessage = StringUtils.toStringOrEmpty(modelParam.getString("message"));

            // 开启多轮对话
            boolean enableMemory = modelParam.getBooleanValue("enable_memory", false);
            List<ChatMessage> messages = CollectionUtils.newArrayList();
            if (BooleanUtils.isTrue(enableMemory) && StringUtils.isNotEmpty(bo.getConversationId())) {
                Query query = new Query();
                query.addCriteria(Criteria.where("conversationId").is(bo.getConversationId()));
                Pageable pageable = PageRequest.of(0, 2);
                query.with(pageable);
                query.with(Sort.by(Sort.Direction.DESC, "requestTime"));
                List<AigcChatMessageEntity> chatMessageList = MongoUtils.selectList(query, AigcChatMessageEntity.class);
                if (CollectionUtils.isNotEmpty(chatMessageList)) {
                    for (AigcChatMessageEntity chatMessage : chatMessageList) {
                        messages.add(ChatMessage.builder().role(ROLE_USER).content(chatMessage.getUserMessage()).build());
                        messages.add(ChatMessage.builder().role(ROLE_ASSISTANT).content(chatMessage.getAssistantMessage()).build());
                    }
                }
            }
            AigcModelEntity model = aigcModelService.getModel(provider, modelName);
            if (null == model) {
                log.error("chat =============== provider:{},model:{} is not exist", provider, modelName);
                return;
            }

            AigcChatConversationEntity conversationEntity = MongoUtils.getById(conversationId, AigcChatConversationEntity.class);
            if (null == conversationEntity) {
                AigcChatConversationEntity conversation = new AigcChatConversationEntity();
                conversation.setId(conversationId);
                conversation.setCreateTime(new Date());
                conversation.setTitle(userMessage);
                conversation.setUserId(loginUserId);
                MongoUtils.save(conversation);
            }

            UserModelUsage userModelUsage = new UserModelUsage();
            userModelUsage.setId(IdUtil.fastSimpleUUID());
            userModelUsage.setUserId(loginUserId);
            userModelUsage.setUserName(loginUserName);
            userModelUsage.setProvider(provider);
            userModelUsage.setModelName(modelName);
            userModelUsage.setConversationId(conversationId);
            userModelUsage.setMessages(messages);
            userModelUsage.setRequestTime(new Date());
            userModelUsage.setModelFunction(bo.getModelFunction());
            userModelUsage.setUserMessage(userMessage);
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (null != attributes) {
                HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
                String requestIp = JakartaServletUtil.getClientIP(servletRequest);
                userModelUsage.setIp(requestIp);
            }
            userModelUsage.setModel(model);

            ChatModelBuilder builder = ChatModelBuilder.builder().build();
            builder.setConversationId(conversationId);
            builder.setUserId(loginUserId);
            builder.setUserName(loginUserName);
            builder.setBaseUrl(model.getBaseUrl());
            builder.setApiKey(model.getApiKey());
            builder.setProvider(provider);
            builder.setModelName(modelName);
            builder.setModelParam(modelParam);
            builder.setUserToken(userModelUsage);
            if (StringUtils.contains(model.getBaseUrl(), "openrouter")) {
                OpenRouterService modelService = SpringUtils.getBean(OpenRouterService.class);
                modelService.chat(builder, response);
            } else {
                ModelService modelService = AigcModelFactory.getModelService(provider);
                modelService.chat(builder, response);
            }
        } catch (Exception e) {
            log.error("chat ==================== error:{}", e.getMessage());
        }
    }

    @Override
    public void model(CommonModelBo bo, HttpServletResponse response) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF_8);

            String modelFunction = bo.getModelFunction();
            String provider = bo.getProvider();
            String modelName = bo.getModel();
            LoginUser loginUser = AuthUtils.getLoginUser();

            AigcModelEntity model = aigcModelService.getModel(provider, modelName);
            if (null == model) {
                log.error("model =============== provider:{},model:{} is not exist", provider, modelName);
                return;
            }

            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            UserModelUsage usage = new UserModelUsage();
            usage.setId(IdUtil.fastSimpleUUID());
            usage.setUserId(loginUser.getId());
            usage.setUserName(loginUser.getRealName());
            usage.setProvider(provider);
            usage.setModelName(modelName);
            usage.setRequestTime(new Date());
            usage.setModelFunction(bo.getModelFunction());
            if (null != attributes) {
                HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
                String requestIp = JakartaServletUtil.getClientIP(servletRequest);
                usage.setIp(requestIp);
            }
            usage.setModel(model);

            CommonModelBuilder builder = CommonModelBuilder.builder().build();
            builder.setUserId(loginUser.getId());
            builder.setUserName(loginUser.getRealName());
            builder.setBaseUrl(model.getBaseUrl());
            builder.setApiKey(model.getApiKey());
            builder.setProvider(provider);
            builder.setModelName(modelName);
            builder.setModelParam(bo.getModelParam());
            builder.setUserModelUsage(usage);
            ModelService modelService = AigcModelFactory.getModelService(provider);

            if (modelFunction.startsWith("image")) {
                modelService.image(builder, response);
            } else if (modelFunction.startsWith("video")) {
                modelService.video(builder, response);
            } else if (modelFunction.startsWith("audio")) {
                modelService.audio(builder, response);
            }
        } catch (Exception e) {
            log.error("audioTranscription ==================== error:{}", e.getMessage());
        }

    }

    private final AigcModelService aigcModelService;

}
