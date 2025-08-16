package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.vo.EmbeddingFileReq;
import com.shuyoutech.aigc.domain.vo.EmbeddingSearchReq;
import com.shuyoutech.aigc.domain.vo.EmbeddingTextReq;

import java.util.List;
import java.util.Map;

/**
 * @author YangChao
 * @date 2025-05-05 09:30
 **/
public interface AiEmbeddingService {

    void embeddingText(EmbeddingTextReq req);

    void embeddingFile(EmbeddingFileReq req);

    List<Map<String, Object>> embeddingSearch(EmbeddingSearchReq req);

}
