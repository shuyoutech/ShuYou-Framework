package com.shuyoutech.aigc.service;

import cn.hutool.core.util.DesensitizedUtil;
import com.alibaba.fastjson2.JSON;
import com.shuyoutech.aigc.constant.AiConstants;
import com.shuyoutech.aigc.domain.bo.AigcVectorStoreBo;
import com.shuyoutech.aigc.domain.entity.AigcVectorStoreEntity;
import com.shuyoutech.aigc.domain.vo.AigcVectorStoreVo;
import com.shuyoutech.aigc.enums.VectorStoreTypeEnum;
import com.shuyoutech.api.service.RemoteSystemService;
import com.shuyoutech.common.cache.enums.CacheMsgTypeEnum;
import com.shuyoutech.common.core.enums.DictTypeEnum;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.model.ParamUnique;
import com.shuyoutech.common.core.util.CollectionUtils;
import com.shuyoutech.common.core.util.MapstructUtils;
import com.shuyoutech.common.core.util.StringUtils;
import com.shuyoutech.common.redis.constant.CacheConstants;
import com.shuyoutech.common.redis.model.RedisMessage;
import com.shuyoutech.common.redis.util.RedisUtils;
import com.shuyoutech.common.web.service.SuperServiceImpl;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.CheckHealthResponse;
import io.milvus.param.ConnectParam;
import io.milvus.param.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author YangChao
 * @date 2025-07-11 13:44:53
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class AigcVectorStoreServiceImpl extends SuperServiceImpl<AigcVectorStoreEntity, AigcVectorStoreVo> implements AigcVectorStoreService {

    @Override
    public List<AigcVectorStoreVo> convertTo(List<AigcVectorStoreEntity> list) {
        List<AigcVectorStoreVo> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        Map<String, String> typeMap = remoteSystemService.translateByDictCode(DictTypeEnum.VECTOR_STORE_TYPE.getValue());
        list.forEach(e -> {
            AigcVectorStoreVo vo = MapstructUtils.convert(e, this.voClass);
            if (null != vo) {
                vo.setTypeName(typeMap.getOrDefault(e.getType(), ""));
                vo.setPassword(DesensitizedUtil.password(e.getPassword()));
                result.add(vo);
            }
        });
        return result;
    }

    public AigcVectorStoreVo convertTo(AigcVectorStoreEntity entity) {
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Query buildQuery(AigcVectorStoreBo bo) {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getName())) {
            query.addCriteria(Criteria.where("name").regex(Pattern.compile(String.format("^.*%s.*$", bo.getName()), Pattern.CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotBlank(bo.getType())) {
            query.addCriteria(Criteria.where("type").is(bo.getType()));
        }
        return query;
    }

    @Override
    public boolean checkUnique(ParamUnique paramUnique) {
        Query query = new Query();
        query.addCriteria(Criteria.where(paramUnique.getParamCode()).is(paramUnique.getParamValue()));
        AigcVectorStoreEntity role = this.selectOne(query);
        if (null == role) {
            return true;
        }
        return StringUtils.isNotBlank(paramUnique.getId()) && paramUnique.getId().equals(role.getId());
    }

    @Override
    public PageResult<AigcVectorStoreVo> page(PageQuery<AigcVectorStoreBo> pageQuery) {
        PageQuery<Query> page = pageQuery.buildPage();
        page.setQuery(buildQuery(pageQuery.getQuery()));
        return this.selectPageVo(page);
    }

    @Override
    public AigcVectorStoreVo detail(String id) {
        AigcVectorStoreEntity entity = this.getById(id);
        return convertTo(entity);
    }

    @Override
    public String saveAiVectorStore(AigcVectorStoreBo bo) {
        AigcVectorStoreEntity entity = this.save(bo);
        RedisUtils.convertAndSend(CacheConstants.SHUYOU_PATTERN_TOPIC, RedisMessage.of(AiConstants.VECTOR_STORE, CacheMsgTypeEnum.UPDATE.getValue(), entity.getId(), JSON.toJSONString(entity)));
        return entity.getId();
    }

    @Override
    public boolean updateAiVectorStore(AigcVectorStoreBo bo) {
        boolean patch = this.patch(bo);
        if (!patch) {
            return false;
        }
        AigcVectorStoreEntity entity = getById(bo.getId());
        RedisUtils.convertAndSend(CacheConstants.SHUYOU_PATTERN_TOPIC, RedisMessage.of(AiConstants.VECTOR_STORE, CacheMsgTypeEnum.UPDATE.getValue(), entity.getId(), JSON.toJSONString(entity)));
        return true;
    }

    @Override
    public boolean deleteAiVectorStore(List<String> ids) {
        boolean flag = this.deleteByIds(ids);
        if (!flag) {
            return false;
        }
        for (String id : ids) {
            RedisUtils.convertAndSend(CacheConstants.SHUYOU_PATTERN_TOPIC, RedisMessage.of(AiConstants.VECTOR_STORE, CacheMsgTypeEnum.DELETE.getValue(), id, ""));
        }
        return true;
    }

    @Override
    public boolean testConnect(String id) {
        try {
            AigcVectorStoreEntity vectorDb = this.getById(id);
            if (null == vectorDb) {
                return false;
            }
            if (VectorStoreTypeEnum.PGVECTOR.getValue().equalsIgnoreCase(vectorDb.getType())) {
                PGSimpleDataSource source = new PGSimpleDataSource();
                source.setServerNames(new String[]{vectorDb.getHost()});
                source.setPortNumbers(new int[]{vectorDb.getPort()});
                source.setDatabaseName(vectorDb.getDatabaseName());
                source.setUser(vectorDb.getUsername());
                source.setPassword(vectorDb.getPassword());

                Connection connection = source.getConnection();
                return !connection.isClosed();
            } else if (VectorStoreTypeEnum.MILVUS.name().equalsIgnoreCase(vectorDb.getType())) {
                ConnectParam.Builder connectBuilder = ConnectParam.newBuilder() //
                        .withHost(vectorDb.getHost()) //
                        .withPort(vectorDb.getPort()) //
                        .withAuthorization(vectorDb.getUsername(), vectorDb.getPassword()) //
                        .withDatabaseName(vectorDb.getDatabaseName());
                MilvusServiceClient client = new MilvusServiceClient(connectBuilder.build());
                R<CheckHealthResponse> checkHealthResponse = client.checkHealth();
                return null != checkHealthResponse && checkHealthResponse.getStatus() == R.Status.Success.getCode();
            } else if (VectorStoreTypeEnum.ELASTICSEARCH.name().equalsIgnoreCase(vectorDb.getType())) {
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                RestClientBuilder builder = RestClient.builder(new HttpHost(vectorDb.getHost(), vectorDb.getPort()));
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(vectorDb.getUsername(), vectorDb.getPassword()));
                // 为 RestClient 设置凭证提供者
                builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
                // 配置请求超时等参数
                builder.setRequestConfigCallback(requestConfig -> {
                    requestConfig.setConnectTimeout(5000);  // 连接超时
                    requestConfig.setSocketTimeout(60000);  // 套接字超时
                    return requestConfig;
                });
                RestClient client = builder.build();
                return client.isRunning();
            }
        } catch (Exception e) {
            log.error("向量数据库连接失败, 错误信息:{}", e.getMessage());
        }
        return false;
    }

    private final RemoteSystemService remoteSystemService;

}