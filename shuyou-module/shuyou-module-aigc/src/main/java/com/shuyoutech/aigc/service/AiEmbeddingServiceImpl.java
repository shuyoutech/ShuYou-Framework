package com.shuyoutech.aigc.service;

import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.shuyoutech.aigc.constant.AiConstants;
import com.shuyoutech.aigc.domain.entity.AigcKnowledgeDocEntity;
import com.shuyoutech.aigc.domain.entity.AigcKnowledgeDocSliceEntity;
import com.shuyoutech.aigc.domain.vo.*;
import com.shuyoutech.aigc.provider.AigcEmbeddingProvider;
import com.shuyoutech.aigc.provider.AigcModelFactory;
import com.shuyoutech.aigc.provider.AigcVectorStoreFactory;
import com.shuyoutech.api.model.RemoteSysFile;
import com.shuyoutech.api.service.RemoteSystemService;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.FileUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.mongodb.MongoUtils;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

/**
 * @author YangChao
 * @date 2025-05-15 10:38
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AiEmbeddingServiceImpl implements AiEmbeddingService {

    @Override
    public void embeddingText(EmbeddingTextReq req) {
        AigcKnowledgeDocEntity doc = new AigcKnowledgeDocEntity();
        doc.setId(IdUtil.getSnowflakeNextIdStr());
        doc.setKnowledgeId(req.getKnowledgeId());
        doc.setDocType("text");
        doc.setDocContent(req.getDocContent());
        doc.setDocName(req.getDocName());
        doc.setDocFileId("");
        doc.setSliceStatus(false);
        doc.setSliceNum(0);
        MongoUtils.save(doc);

        EmbeddingText embeddingText = aigcEmbeddingProvider.embeddingText(new EmbeddingDocReq().setKnowledgeId(req.getKnowledgeId()).setDocId(doc.getId()).setMessage(req.getDocContent()));
        if (null == embeddingText) {
            throw new BusinessException("Text文本向量解析失败");
        }

        AigcKnowledgeDocSliceEntity slice = new AigcKnowledgeDocSliceEntity();
        slice.setId(IdUtil.getSnowflakeNextIdStr());
        slice.setKnowledgeId(req.getKnowledgeId());
        slice.setDocId(doc.getId());
        slice.setDocName(doc.getDocName());
        slice.setVectorId(embeddingText.getVectorId());
        slice.setContent(embeddingText.getText());
        MongoUtils.save(slice);

        Update update = new Update();
        update.set("sliceStatus", true);
        update.set("sliceNum", 1);
        MongoUtils.patch(doc.getId(), update, AigcKnowledgeDocEntity.class);
    }

    @Override
    public void embeddingFile(EmbeddingFileReq req) {
        List<String> docFileIds = req.getDocFileIds();
        String knowledgeId = req.getKnowledgeId();
        for (String docFileId : docFileIds) {
            dealEmbeddingFile(knowledgeId, docFileId);
        }
    }

    private void dealEmbeddingFile(String knowledgeId, String docFileId) {
        RemoteSysFile file = remoteSystemService.getFileById(docFileId);
        if (null == file) {
            log.error("dealEmbeddingFile ============ docFileId:{} is not exist", docFileId);
            return;
        }
        Date now = new Date();

        AigcKnowledgeDocEntity doc = new AigcKnowledgeDocEntity();
        doc.setId(IdUtil.getSnowflakeNextIdStr());
        doc.setCreateTime(now);
        doc.setKnowledgeId(knowledgeId);
        doc.setDocType("file");
        doc.setDocName(file.getOriginalFileName());
        doc.setDocContent("");
        doc.setDocFileId(docFileId);
        doc.setSliceStatus(false);
        doc.setSliceNum(0);
        MongoUtils.save(doc);

        ThreadUtil.execute(() -> {
            if ("csv".equalsIgnoreCase(file.getFileType())) {
                String filePath = remoteSystemService.getFilePath(docFileId);
                if (StringUtils.isBlank(filePath)) {
                    log.error("dealEmbeddingFile ============ docFileId:{} filePath is null", docFileId);
                    return;
                }
                try {
                    CsvReader reader = CsvUtil.getReader();
                    // 从文件中读取CSV数据
                    CsvData data = reader.read(new File(filePath));
                    List<CsvRow> rows = data.getRows();
                    // 遍历行
                    for (CsvRow csvRow : rows) {
                        //getRawList返回一个List列表，列表的每一项为CSV中的一个单元格（既逗号分隔部分）
                        EmbeddingText embeddingText = aigcEmbeddingProvider.embeddingText(new EmbeddingDocReq().setKnowledgeId(knowledgeId).setDocId(doc.getId()).setMessage(csvRow.get(0) + " " + csvRow.get(1)));
                        if (null == embeddingText) {
                            continue;
                        }
                        AigcKnowledgeDocSliceEntity slice = new AigcKnowledgeDocSliceEntity();
                        slice.setId(IdUtil.getSnowflakeNextIdStr());
                        slice.setCreateTime(now);
                        slice.setDocId(doc.getId());
                        slice.setDocName(doc.getDocName());
                        slice.setKnowledgeId(knowledgeId);
                        slice.setVectorId(embeddingText.getVectorId());
                        slice.setContent(embeddingText.getText());
                        MongoUtils.save(slice);
                    }
                    Update update = new Update();
                    update.set("sliceStatus", true);
                    update.set("sliceNum", rows.size());
                    MongoUtils.patch(doc.getId(), update, AigcKnowledgeDocEntity.class);
                } catch (Exception e) {
                    log.error("embeddingFile --------- exception:{}", e.getMessage());
                } finally {
                    FileUtils.del(filePath);
                }
            } else {
                List<EmbeddingText> embeddingTexts = aigcEmbeddingProvider.embeddingDoc(new EmbeddingDocReq().setKnowledgeId(knowledgeId).setDocId(doc.getId()).setFileUrl(file.getPreviewUrl()));
                if (CollectionUtils.isEmpty(embeddingTexts)) {
                    return;
                }
                embeddingTexts.forEach(embeddingText -> {
                    AigcKnowledgeDocSliceEntity slice = new AigcKnowledgeDocSliceEntity();
                    slice.setId(IdUtil.getSnowflakeNextIdStr());
                    slice.setCreateTime(now);
                    slice.setKnowledgeId(knowledgeId);
                    slice.setDocId(doc.getId());
                    slice.setDocName(doc.getDocName());
                    slice.setVectorId(embeddingText.getVectorId());
                    slice.setContent(embeddingText.getText());
                    MongoUtils.save(slice);
                });

                Update update = new Update();
                update.set("sliceStatus", true);
                update.set("sliceNum", embeddingTexts.size());
                MongoUtils.patch(doc.getId(), update, AigcKnowledgeDocEntity.class);
            }
        });
    }

    @Override
    public List<Map<String, Object>> embeddingSearch(EmbeddingSearchReq req) {
        String knowledgeId = req.getKnowledgeId();
        String text = req.getText();
        EmbeddingModel embeddingModel = aigcModelFactory.getEmbeddingModelByKnowledgeId(knowledgeId);
        if (null == embeddingModel) {
            log.error("embeddingSearch embeddingModel is not exist KnowledgeId={}", knowledgeId);
            return null;
        }
        EmbeddingStore<TextSegment> embeddingStore = aigcVectorStoreFactory.getEmbeddingStoreByKnowledgeId(knowledgeId);
        if (null == embeddingStore) {
            log.error("embeddingSearch embeddingStore is not exist KnowledgeId={}", knowledgeId);
            return null;
        }
        Embedding queryEmbedding = embeddingModel.embed(text).content();
        Filter filter = metadataKey(AiConstants.KNOWLEDGE_ID).isEqualTo(knowledgeId);
        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(EmbeddingSearchRequest.builder().queryEmbedding(queryEmbedding).filter(filter).build());
        if (null == result || CollectionUtils.isEmpty(result.matches())) {
            return null;
        }
        List<Map<String, Object>> list = CollectionUtils.newArrayList();
        result.matches().forEach(i -> {
            if (i.score() > 0.8) {
                TextSegment embedded = i.embedded();
                Map<String, Object> map = embedded.metadata().toMap();
                map.put("text", embedded.text());
                map.put("score", i.score());
                list.add(map);
            }
        });
        return list;
    }

    private final AigcEmbeddingProvider aigcEmbeddingProvider;
    private final RemoteSystemService remoteSystemService;
    private final AigcModelFactory aigcModelFactory;
    private final AigcVectorStoreFactory aigcVectorStoreFactory;

}
