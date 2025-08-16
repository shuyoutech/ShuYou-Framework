package com.shuyoutech.aigc.provider;

import com.shuyoutech.aigc.constant.AiConstants;
import com.shuyoutech.aigc.domain.vo.EmbeddingDocReq;
import com.shuyoutech.aigc.domain.vo.EmbeddingText;
import com.shuyoutech.common.core.util.CollectionUtils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-05-14 16:02
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AigcEmbeddingProvider {

    public EmbeddingText embeddingText(EmbeddingDocReq req) {
        // log.info("-------------- Text文本向量解析开始，KnowledgeId={}, DocId={}", req.getKnowledgeId(), req.getDocId());
        TextSegment segment = TextSegment.from(req.getMessage(), Metadata.metadata(AiConstants.KNOWLEDGE_ID, req.getKnowledgeId()).put(AiConstants.DOC_ID, req.getDocId()));
        EmbeddingModel embeddingModel = aigcModelFactory.getEmbeddingModelByKnowledgeId(req.getKnowledgeId());
        if (null == embeddingModel) {
            log.error("embeddingText embeddingModel is not exist KnowledgeId={}, DocId={}", req.getKnowledgeId(), req.getDocId());
            return null;
        }
        EmbeddingStore<TextSegment> embeddingStore = aigcVectorStoreFactory.getEmbeddingStoreByKnowledgeId(req.getKnowledgeId());
        if (null == embeddingStore) {
            log.error("embeddingText embeddingStore is not exist KnowledgeId={}, DocId={}", req.getKnowledgeId(), req.getDocId());
            return null;
        }
        Response<Embedding> response = embeddingModel.embed(segment);
        TokenUsage tokenUsage = response.tokenUsage();
        Embedding embedding = response.content();
        String id = embeddingStore.add(embedding, segment);
        // log.info("-------------- Text文本向量解析结束，KnowledgeId={}, DocId={}, tokenUsage:{}", req.getKnowledgeId(), req.getDocId(), tokenUsage.toString());
        return new EmbeddingText().setVectorId(id).setText(segment.text()).setKnowledgeId(req.getKnowledgeId());
    }

    public List<EmbeddingText> embeddingDoc(EmbeddingDocReq req) {
        // log.info("-------------- Doc文档向量解析开始，KnowledgeId:{}, DocId:{}", req.getKnowledgeId(), req.getDocId());
        Document document = UrlDocumentLoader.load(req.getFileUrl(), new ApacheTikaDocumentParser());
        document.metadata().put(AiConstants.KNOWLEDGE_ID, req.getKnowledgeId()).put(AiConstants.DOC_ID, req.getDocId());
        List<EmbeddingText> list = CollectionUtils.newArrayList();
        try {
            // maxSegmentSizeInChars 每段最长字数, maxOverlapSizeInChars自然语言最大重叠字段-保证一段话完整性
            DocumentSplitter splitter = DocumentSplitters.recursive(300, 20);
            List<TextSegment> segments = splitter.split(document);
            EmbeddingModel embeddingModel = aigcModelFactory.getEmbeddingModelByKnowledgeId(req.getKnowledgeId());
            if (null == embeddingModel) {
                log.error("embeddingDoc embeddingModel is not exist KnowledgeId={}, DocId={}", req.getKnowledgeId(), req.getDocId());
                return null;
            }
            EmbeddingStore<TextSegment> embeddingStore = aigcVectorStoreFactory.getEmbeddingStoreByKnowledgeId(req.getKnowledgeId());
            if (null == embeddingStore) {
                log.error("embeddingDoc embeddingStore is not exist KnowledgeId={}, DocId={}", req.getKnowledgeId(), req.getDocId());
                return null;
            }
            Response<List<Embedding>> listResponse = embeddingModel.embedAll(segments);
            TokenUsage tokenUsage = listResponse.tokenUsage();
            List<Embedding> embeddings = listResponse.content();
            List<String> ids = embeddingStore.addAll(embeddings, segments);
            if (CollectionUtils.isEmpty(ids)) {
                return null;
            }
            for (int i = 0; i < ids.size(); i++) {
                list.add(new EmbeddingText().setVectorId(ids.get(i)).setText(segments.get(i).text()).setKnowledgeId(req.getKnowledgeId()).setDocId(req.getDocId()));
            }
            // log.info("-------------- Doc文档向量解析结束，KnowledgeId:{}, DocId:{}, tokenUsage:{}", req.getKnowledgeId(), req.getDocId(), tokenUsage.toString());
        } catch (Exception e) {
            log.error("embeddingDoc ------------- exception:{}", e.getMessage());
        }
        return list;
    }

    private final AigcModelFactory aigcModelFactory;
    private final AigcVectorStoreFactory aigcVectorStoreFactory;

}
