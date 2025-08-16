package com.shuyoutech.aigc.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.shuyoutech.aigc.constant.AiConstants;
import com.shuyoutech.aigc.domain.bo.AigcAppBo;
import com.shuyoutech.aigc.domain.bo.AigcChatCompletionsBo;
import com.shuyoutech.aigc.domain.bo.AigcChatTestBo;
import com.shuyoutech.aigc.domain.entity.AigcAppEntity;
import com.shuyoutech.aigc.domain.model.AppChatMessageData;
import com.shuyoutech.aigc.domain.vo.AigcAppVo;
import com.shuyoutech.aigc.domain.vo.AigcKnowledgeVo;
import com.shuyoutech.aigc.enums.AiSourceTypeEnum;
import com.shuyoutech.aigc.enums.MessageRoleEnum;
import com.shuyoutech.aigc.enums.MessageTypeEnum;
import com.shuyoutech.aigc.provider.AigcAppFactory;
import com.shuyoutech.aigc.provider.AigcKnowledgeFactory;
import com.shuyoutech.aigc.provider.AigcModelFactory;
import com.shuyoutech.aigc.provider.AigcVectorStoreFactory;
import com.shuyoutech.api.service.RemoteSystemService;
import com.shuyoutech.common.cache.enums.CacheMsgTypeEnum;
import com.shuyoutech.common.core.enums.DictTypeEnum;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.disruptor.model.DisruptorData;
import com.shuyoutech.common.redis.constant.CacheConstants;
import com.shuyoutech.common.redis.model.RedisMessage;
import com.shuyoutech.common.redis.util.RedisUtils;
import com.shuyoutech.common.satoken.util.AuthUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;

import static com.shuyoutech.aigc.constant.AiConstants.EVENT_ANSWER;
import static com.shuyoutech.aigc.provider.AigcModelFactory.MODEL_MAP;
import static com.shuyoutech.common.disruptor.init.DisruptorRunner.disruptorProducer;
import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

/**
 * @author YangChao
 * @date 2025-07-12 09:29:09
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AigcAppServiceImpl extends SuperServiceImpl<AigcAppEntity, AigcAppVo> implements AigcAppService {

    @Override
    public List<AigcAppVo> convertTo(List<AigcAppEntity> list) {
        List<AigcAppVo> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        Map<String, String> appMap = remoteSystemService.translateByDictCode(DictTypeEnum.AI_APP_TYPE.getValue());
        list.forEach(e -> {
            AigcAppVo vo = MapstructUtils.convert(e, this.voClass);
            if (null != vo) {
                vo.setAppTypeName(appMap.getOrDefault(e.getAppType(), ""));
                if (StringUtils.isNotBlank(e.getKnowledgeId())) {
                    vo.setKnowledge(aigcKnowledgeFactory.getKnowledge(e.getKnowledgeId()));
                }
                if (StringUtils.isNotBlank(e.getChatModelId())) {
                    vo.setChatModel(MODEL_MAP.get(e.getChatModelId()));
                }
                result.add(vo);
            }
        });
        return result;
    }

    public AigcAppVo convertTo(AigcAppEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(AigcAppBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getAppName())) {
            query.addCriteria(Criteria.where("appName").regex(Pattern.compile(String.format("^.*%s.*$", bo.getAppName()), Pattern.CASE_INSENSITIVE)));
        }
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        AigcAppEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<AigcAppVo> page(PageQuery<AigcAppBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public AigcAppVo detail(String id) {
        AigcAppEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveAiApp(AigcAppBo bo) {
        AigcAppEntity entity = this.save(bo);
        RedisUtils.convertAndSend(CacheConstants.SHUYOU_PATTERN_TOPIC, RedisMessage.of(AiConstants.APP, CacheMsgTypeEnum.UPDATE.getValue(), entity.getId(), JSON.toJSONString(entity)));
        return entity.getId();
    }

    @Override
    public boolean updateAiApp(AigcAppBo bo) {
        boolean patch = this.patch(bo);
        if (!patch) {
            return false;
        }
        AigcAppEntity entity = getById(bo.getId());
        if (null == entity) {
            return false;
        }
        RedisUtils.convertAndSend(CacheConstants.SHUYOU_PATTERN_TOPIC, RedisMessage.of(AiConstants.APP, CacheMsgTypeEnum.UPDATE.getValue(), entity.getId(), JSON.toJSONString(entity)));
        return true;
    }

    @Override
    public boolean deleteAiApp(List<String> ids) {
        boolean flag = this.deleteByIds(ids);
        if (!flag) {
            return false;
        }
        for (String id : ids) {
            RedisUtils.convertAndSend(CacheConstants.SHUYOU_PATTERN_TOPIC, RedisMessage.of(AiConstants.APP, CacheMsgTypeEnum.DELETE.getValue(), id, ""));
        }
        return true;
    }

    @Override
    public void chatTest(AigcChatTestBo bo, HttpServletResponse response) {
        String appId = bo.getAppId();
        String chatModelId = bo.getChatModelId();
        String knowledgeId = bo.getKnowledgeId();
        String loginUserId = AuthUtils.getLoginUserId();
        String promptText = bo.getPromptText();
        String conversationId = StringUtils.isEmpty(bo.getConversationId()) ? IdUtil.simpleUUID() : bo.getConversationId();
        bo.setConversationId(conversationId);

        // 保存聊天消息
        AppChatMessageData disruptorChatMessageData = new AppChatMessageData();
        disruptorChatMessageData.setAppId(appId);
        disruptorChatMessageData.setConversationId(conversationId);
        disruptorChatMessageData.setMessageContent(bo.getMessages().getFirst().getContent());
        disruptorChatMessageData.setMessageType(MessageTypeEnum.TEXT.getValue());
        disruptorChatMessageData.setMessageRole(MessageRoleEnum.USER.getValue());
        disruptorChatMessageData.setSource(AiSourceTypeEnum.TEST.getValue());
        disruptorChatMessageData.setUserId(loginUserId);
        DisruptorData disruptorData = new DisruptorData();
        disruptorData.setServiceName(AiConstants.APP_CHAT_SERVICE);
        disruptorData.setData(disruptorChatMessageData);
        disruptorProducer.pushData(disruptorData);

        AigcAppVo app = aigcAppFactory.getApp(appId);
        if (null == app) {
            log.error("chatTest ========= app:{} is not exist", appId);
            throw new BusinessException(StringUtils.format("app:{} is not exist", appId));
        }

        AigcKnowledgeVo knowledge = aigcKnowledgeFactory.getKnowledge(knowledgeId);
        if (null == knowledge) {
            log.error("chatTest ========= knowledgeId:{} is not exist", knowledgeId);
            throw new BusinessException(StringUtils.format("knowledgeId:{} is not exist", knowledgeId));
        }
        EmbeddingModel embeddingModel = aigcModelFactory.getEmbeddingModelByKnowledgeId(knowledgeId);
        if (null == embeddingModel) {
            log.error("chatTest ========= chat embeddingModel is not exist KnowledgeId={}", knowledgeId);
            throw new BusinessException(StringUtils.format("chat embeddingModel is not exist KnowledgeId={}", knowledgeId));
        }
        EmbeddingStore<TextSegment> embeddingStore = aigcVectorStoreFactory.getEmbeddingStoreByKnowledgeId(knowledgeId);
        if (null == embeddingStore) {
            log.error("chatTest ========= chat embeddingStore is not exist KnowledgeId={}", knowledgeId);
            throw new BusinessException(StringUtils.format("chat embeddingStore is not exist KnowledgeId={}", knowledgeId));
        }
        StreamingChatModel streamingChatModel = aigcModelFactory.getStreamingChatModel(chatModelId);
        if (null == streamingChatModel) {
            log.error("chatTest ========= chat streamingChatModel is not exist appId={}", appId);
            throw new BusinessException(StringUtils.format("chat streamingChatModel is not exist appId={}", appId));
        }

        // 知识库条件过滤
        Function<dev.langchain4j.rag.query.Query, Filter> filter = (query) -> metadataKey(AiConstants.KNOWLEDGE_ID).isEqualTo(knowledgeId);
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder() //
                .embeddingStore(embeddingStore) //
                .embeddingModel(embeddingModel) //
                .dynamicFilter(filter) //
                .minScore(0.75) //
                .build();

        // AI services
        AiServices<AiAgentAssistant> aiServices = AiServices.builder(AiAgentAssistant.class) //
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder() //
                        .id(conversationId) //
                        .chatMemoryStore(new PersistentChatMemoryStore()) //
                        .maxMessages(20) //
                        .build());
        if (StrUtil.isNotBlank(promptText)) {
            aiServices.systemMessageProvider(memoryId -> promptText);
        }
        aiServices.streamingChatModel(streamingChatModel);
        aiServices.retrievalAugmentor(DefaultRetrievalAugmentor.builder() //
                .contentRetriever(contentRetriever) //
                .build());

        AiAgentAssistant agent = aiServices.build();
        TokenStream tokenStream = agent.chat(conversationId, bo.getMessages().getFirst().getContent());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {

            // rag回调
            tokenStream.onRetrieved(contents -> {
                List<Map<String, Object>> list = new ArrayList<>();
                contents.forEach(content -> {
                    TextSegment textSegment = content.textSegment();
                    Map<String, Object> map = textSegment.metadata().toMap();
                    map.put("text", textSegment.text());
                    list.add(map);
                });
                // 前端可监听Retrieved时间，展示命中的文件
                try {
                    response.getWriter().write("event:retrieved");
                    response.getWriter().println();
                    response.getWriter().write("data: " + JSON.toJSONString(list));
                    response.getWriter().println();
                    response.getWriter().println();
                    response.getWriter().flush();
                } catch (Exception ignore) {
                }
            });
            //消息片段回调
            tokenStream.onPartialResponse(partialResponse -> {
                try {
                    JSONObject object = new JSONObject();
                    object.put("type", "text");
                    object.put("value", partialResponse);
                    response.getWriter().write("event:" + EVENT_ANSWER);
                    response.getWriter().println();
                    response.getWriter().write("data: " + object.toJSONString());
                    response.getWriter().println();
                    response.getWriter().println();
                    response.getWriter().flush();
                } catch (Exception ignore) {
                }
            });
            //错误回调
            tokenStream.onError(error -> {
                log.error("onEvent ====================== error:{}", error.getMessage());
                countDownLatch.countDown();
            });
            //结束回调
            tokenStream.onCompleteResponse(aiMessageResponse -> {
                TokenUsage tokenUsage = aiMessageResponse.tokenUsage();
                log.info("chatTest ========= token usage inputTokenCount:{},outputTokenCount:{},totalTokenCount:{}", tokenUsage.inputTokenCount(), tokenUsage.outputTokenCount(), tokenUsage.totalTokenCount());
                AppChatMessageData disruptorChatMessageData2 = new AppChatMessageData();
                disruptorChatMessageData2.setAppId(appId);
                disruptorChatMessageData2.setConversationId(conversationId);
                disruptorChatMessageData2.setMessageContent(aiMessageResponse.aiMessage().text());
                disruptorChatMessageData2.setMessageType(MessageTypeEnum.TEXT.getValue());
                disruptorChatMessageData2.setMessageRole(MessageRoleEnum.ASSISTANT.getValue());
                disruptorChatMessageData2.setSource(AiSourceTypeEnum.TEST.getValue());
                disruptorChatMessageData2.setUserId(loginUserId);
                DisruptorData disruptorData2 = new DisruptorData();
                disruptorData2.setServiceName(AiConstants.APP_CHAT_SERVICE);
                disruptorData2.setData(disruptorChatMessageData2);
                disruptorProducer.pushData(disruptorData2);
                countDownLatch.countDown();
            });
            tokenStream.start();
            boolean await = countDownLatch.await(5, TimeUnit.MINUTES);
            if (!await) {
                log.error("chatTest ============================ getCountDownLatch timed out");
            }
        } catch (Exception e) {
            log.error("onEvent ====================== exception:{}", e.getMessage());
        }
    }

    @Override
    public void completions(AigcChatCompletionsBo bo, HttpServletResponse response) {
        String appId = bo.getAppId();
        String conversationId = StringUtils.isEmpty(bo.getConversationId()) ? IdUtil.simpleUUID() : bo.getConversationId();
        bo.setConversationId(conversationId);
        String loginUserId = AuthUtils.getLoginUserId();

        // 保存聊天消息
        AppChatMessageData disruptorChatMessageData = new AppChatMessageData();
        disruptorChatMessageData.setAppId(appId);
        disruptorChatMessageData.setConversationId(conversationId);
        disruptorChatMessageData.setMessageContent(bo.getMessages().getFirst().getContent());
        disruptorChatMessageData.setMessageType(MessageTypeEnum.TEXT.getValue());
        disruptorChatMessageData.setMessageRole(MessageRoleEnum.USER.getValue());
        disruptorChatMessageData.setSource(AiSourceTypeEnum.ONLINE.getValue());
        disruptorChatMessageData.setUserId(loginUserId);
        DisruptorData disruptorData = new DisruptorData();
        disruptorData.setServiceName(AiConstants.APP_CHAT_SERVICE);
        disruptorData.setData(disruptorChatMessageData);
        disruptorProducer.pushData(disruptorData);

        AigcAppVo app = aigcAppFactory.getApp(appId);
        if (null == app) {
            log.error("completions ========= app:{} is not exist", appId);
            throw new BusinessException(StringUtils.format("app:{} is not exist", appId));
        }
        String knowledgeId = app.getKnowledgeId();
        AigcKnowledgeVo knowledge = app.getKnowledge();
        if (null == knowledge) {
            log.error("completions ========= knowledgeId:{} is not exist", knowledgeId);
            throw new BusinessException(StringUtils.format("knowledgeId:{} is not exist", knowledgeId));
        }
        EmbeddingModel embeddingModel = aigcModelFactory.getEmbeddingModelByKnowledgeId(knowledgeId);
        if (null == embeddingModel) {
            log.error("completions ========= chat embeddingModel is not exist KnowledgeId={}", knowledgeId);
            throw new BusinessException(StringUtils.format("chat embeddingModel is not exist KnowledgeId={}", knowledgeId));
        }
        EmbeddingStore<TextSegment> embeddingStore = aigcVectorStoreFactory.getEmbeddingStoreByKnowledgeId(knowledgeId);
        if (null == embeddingStore) {
            log.error("completions ========= chat embeddingStore is not exist KnowledgeId={}", knowledgeId);
            throw new BusinessException(StringUtils.format("chat embeddingStore is not exist KnowledgeId={}", knowledgeId));
        }
        StreamingChatModel streamingChatModel = aigcModelFactory.getStreamingChatModelByAppId(appId);
        if (null == streamingChatModel) {
            log.error("completions ========= chat streamingChatModel is not exist appId={}", appId);
            throw new BusinessException(StringUtils.format("chat streamingChatModel is not exist appId={}", appId));
        }

        Function<dev.langchain4j.rag.query.Query, Filter> filter = (query) -> metadataKey(AiConstants.KNOWLEDGE_ID).isEqualTo(knowledgeId);
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder() //
                .embeddingStore(embeddingStore) //
                .embeddingModel(embeddingModel) //
                .dynamicFilter(filter) //
                .minScore(0.75) //
                .build();

        AiServices<AiAgentAssistant> aiServices = AiServices.builder(AiAgentAssistant.class) //
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder() //
                        .id(conversationId) //
                        .chatMemoryStore(new PersistentChatMemoryStore()) //
                        .maxMessages(20) //
                        .build());

        if (StrUtil.isNotBlank(app.getPromptText())) {
            aiServices.systemMessageProvider(memoryId -> app.getPromptText());
        }

        aiServices.streamingChatModel(streamingChatModel);
        aiServices.retrievalAugmentor(DefaultRetrievalAugmentor.builder() //
                .contentRetriever(contentRetriever) //
                .build());

        AiAgentAssistant agent = aiServices.build();
        TokenStream tokenStream = agent.chat(conversationId, bo.getMessages().getFirst().getContent());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        // rag回调
        tokenStream.onRetrieved(contents -> {
            List<Map<String, Object>> list = new ArrayList<>();
            contents.forEach(content -> {
                TextSegment textSegment = content.textSegment();
                Map<String, Object> map = textSegment.metadata().toMap();
                map.put("text", textSegment.text());
                list.add(map);
            });
            // 前端可监听Retrieved时间，展示命中的文件
            try {
                response.getWriter().write("event:retrieved");
                response.getWriter().println();
                response.getWriter().write("data: " + JSON.toJSONString(list));
                response.getWriter().println();
                response.getWriter().println();
                response.getWriter().flush();
            } catch (Exception ignore) {
            }
        });
        //消息片段回调
        tokenStream.onPartialResponse(partialResponse -> {
            try {
                JSONObject object = new JSONObject();
                object.put("type", "text");
                object.put("value", partialResponse);
                response.getWriter().write("event:" + EVENT_ANSWER);
                response.getWriter().println();
                response.getWriter().write("data: " + object.toJSONString());
                response.getWriter().println();
                response.getWriter().println();
                response.getWriter().flush();
            } catch (Exception ignore) {
            }
        });
        //错误回调
        tokenStream.onError(error -> {
            log.error("completions ====================== error:{}", error.getMessage());
            countDownLatch.countDown();
        });
        //结束回调
        tokenStream.onCompleteResponse(aiMessageResponse -> {
            TokenUsage tokenUsage = aiMessageResponse.tokenUsage();
            log.info("completions ========= token usage inputTokenCount:{},outputTokenCount:{},totalTokenCount:{}", tokenUsage.inputTokenCount(), tokenUsage.outputTokenCount(), tokenUsage.totalTokenCount());
            AppChatMessageData disruptorChatMessageData2 = new AppChatMessageData();
            disruptorChatMessageData2.setAppId(appId);
            disruptorChatMessageData2.setConversationId(conversationId);
            disruptorChatMessageData2.setMessageContent(aiMessageResponse.aiMessage().text());
            disruptorChatMessageData2.setMessageType(MessageTypeEnum.TEXT.getValue());
            disruptorChatMessageData2.setMessageRole(MessageRoleEnum.ASSISTANT.getValue());
            disruptorChatMessageData2.setSource(AiSourceTypeEnum.ONLINE.getValue());
            disruptorChatMessageData2.setUserId(loginUserId);

            DisruptorData disruptorData2 = new DisruptorData();
            disruptorData2.setServiceName(AiConstants.APP_CHAT_SERVICE);
            disruptorData2.setData(disruptorChatMessageData2);
            disruptorProducer.pushData(disruptorData2);
            countDownLatch.countDown();
        });
        tokenStream.start();
        try {
            boolean await = countDownLatch.await(5, TimeUnit.MINUTES);
            if (!await) {
                log.error("completions =========================== getCountDownLatch timed out");
            }
        } catch (Exception ignore) {
        }
    }

    private final RemoteSystemService remoteSystemService;
    private final AigcKnowledgeFactory aigcKnowledgeFactory;
    private final AigcAppFactory aigcAppFactory;
    private final AigcModelFactory aigcModelFactory;
    private final AigcVectorStoreFactory aigcVectorStoreFactory;

}