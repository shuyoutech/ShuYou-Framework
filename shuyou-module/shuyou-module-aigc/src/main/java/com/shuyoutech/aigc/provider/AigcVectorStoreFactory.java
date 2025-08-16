package com.shuyoutech.aigc.provider;

import com.shuyoutech.aigc.domain.entity.AigcVectorStoreEntity;
import com.shuyoutech.aigc.domain.vo.AigcKnowledgeVo;
import com.shuyoutech.aigc.enums.VectorStoreTypeEnum;
import com.shuyoutech.aigc.service.AigcVectorStoreService;
import com.shuyoutech.common.core.exception.BusinessException;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author YangChao
 * @date 2025-05-12 14:34
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AigcVectorStoreFactory {

    public static final Map<String, AigcVectorStoreEntity> VECTOR_DB_MAP = new ConcurrentHashMap<>();
    public static final Map<String, EmbeddingStore<TextSegment>> EMBEDDING_TORE_MAP = new ConcurrentHashMap<>();

    public void init() {
        List<AigcVectorStoreEntity> vectorDbList = aigcVectorStoreService.selectList();
        if (CollectionUtils.isEmpty(vectorDbList)) {
            return;
        }
        vectorDbList.forEach(this::buildVectorDb);
    }

    public void buildVectorDb(AigcVectorStoreEntity vectorDb) {
        try {
            if (VectorStoreTypeEnum.PGVECTOR.getValue().equalsIgnoreCase(vectorDb.getType())) {
                EmbeddingStore<TextSegment> store = PgVectorEmbeddingStore.builder() //
                        .host(vectorDb.getHost()) //
                        .port(vectorDb.getPort()) //
                        .database(vectorDb.getDatabaseName()) //
                        .dimension(vectorDb.getDimension()) //
                        .user(vectorDb.getUsername()) //
                        .password(vectorDb.getPassword()) //
                        .table(vectorDb.getTableName()) //
                        .indexListSize(1) //
                        .useIndex(true).createTable(true).dropTableFirst(false).build();
                EMBEDDING_TORE_MAP.put(vectorDb.getId(), store);
                VECTOR_DB_MAP.put(vectorDb.getId(), vectorDb);
            } else if (VectorStoreTypeEnum.MILVUS.name().equalsIgnoreCase(vectorDb.getType())) {
                EmbeddingStore<TextSegment> store = MilvusEmbeddingStore.builder() //
                        .host(vectorDb.getHost()) //
                        .port(vectorDb.getPort()) //
                        .databaseName(vectorDb.getDatabaseName()) //
                        .dimension(vectorDb.getDimension()) //
                        .username(vectorDb.getUsername()) //
                        .password(vectorDb.getPassword()) //
                        .collectionName(vectorDb.getTableName()).build();
                EMBEDDING_TORE_MAP.put(vectorDb.getId(), store);
                VECTOR_DB_MAP.put(vectorDb.getId(), vectorDb);
            } else if (VectorStoreTypeEnum.ELASTICSEARCH.name().equalsIgnoreCase(vectorDb.getType())) {
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(vectorDb.getUsername(), vectorDb.getPassword()));
                RestClientBuilder builder = RestClient.builder(new HttpHost(vectorDb.getHost(), vectorDb.getPort()));
                // 为 RestClient 设置凭证提供者
                builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
                // 配置请求超时等参数
                builder.setRequestConfigCallback(requestConfig -> {
                    requestConfig.setConnectTimeout(5000);  // 连接超时
                    requestConfig.setSocketTimeout(60000);  // 套接字超时
                    return requestConfig;
                });
                EmbeddingStore<TextSegment> store = ElasticsearchEmbeddingStore.builder().restClient(builder.build()).indexName(vectorDb.getTableName()).build();
                EMBEDDING_TORE_MAP.put(vectorDb.getId(), store);
                VECTOR_DB_MAP.put(vectorDb.getId(), vectorDb);
            }
        } catch (Exception e) {
            log.error("向量数据库初始化失败：[{}] --- [{}], 错误信息:{}", vectorDb.getName(), vectorDb.getType(), e.getMessage());
        }
    }

    public EmbeddingStore<TextSegment> getEmbeddingStoreByKnowledgeId(String knowledgeId) {
        AigcKnowledgeVo knowledge = aigcKnowledgeFactory.getKnowledge(knowledgeId);
        if (null == knowledge) {
            throw new BusinessException("没有找到匹配的向量数据库");
        }
        String vectorStoreId = knowledge.getVectorStoreId();
        return getEmbeddingStore(vectorStoreId);
    }

    public EmbeddingStore<TextSegment> getEmbeddingStore(String embeddingId) {
        return EMBEDDING_TORE_MAP.get(embeddingId);
    }

    private final AigcVectorStoreService aigcVectorStoreService;
    private final AigcKnowledgeFactory aigcKnowledgeFactory;

}
