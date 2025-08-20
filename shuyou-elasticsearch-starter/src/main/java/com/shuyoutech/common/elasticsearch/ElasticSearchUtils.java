package com.shuyoutech.common.elasticsearch;

import cn.hutool.core.annotation.AnnotationUtil;
import com.shuyoutech.common.core.constant.EntityConstants;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateResponse;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ElasticSearch工具类
 * <a href="https://www.elastic.co/docs/reference/elasticsearch/clients/java/getting-started">...</a>
 * <a href="https://github.com/elastic/elasticsearch">...</a>
 *
 * @author YangChao
 * @date 2025-08-13 20:52
 **/
@Slf4j
public class ElasticSearchUtils {

    public static final ElasticsearchTemplate elasticsearchTemplate = SpringUtils.getBean("elasticsearchTemplate");

    private static <E> String getIndex(Class<E> clazz) {
        org.springframework.data.elasticsearch.annotations.Document annotation = AnnotationUtil.getAnnotation(clazz, org.springframework.data.elasticsearch.annotations.Document.class);
        String indexName = annotation.indexName();
        if (StringUtils.isEmpty(indexName)) {
            throw new BusinessException("请实体类上面配置indexName");
        }
        return indexName;
    }

    /**
     * 插入数据 如果已存在则更新
     *
     * @param entity 实体类
     * @return 执行记录数
     */
    public static <E> E insert(E entity) {
        return elasticsearchTemplate.save(entity);
    }

    /**
     * 批量插入数据
     *
     * @param list 实体集合
     * @return 执行记录数
     */
    public static <E> Collection<E> insertBatch(Collection<E> list) {
        return CollectionUtils.newArrayList(elasticsearchTemplate.save(list));
    }

    /**
     * 保存对象,存在则更新，不存在则插入
     *
     * @param entity 实体类
     * @return 执行记录数
     */
    public static <E> E save(E entity) {
        return elasticsearchTemplate.save(entity);
    }

    /**
     * 批量保存对象 已存在的删除在插入
     *
     * @param list 实体集合
     * @return 执行记录数
     */
    public static <E> Collection<E> saveBatch(Collection<E> list) {
        return CollectionUtils.newArrayList(elasticsearchTemplate.save(list));
    }

    /**
     * 更新全部字段
     *
     * @param entity 实体类
     * @return 执行记录数
     */
    public static <E> boolean update(E entity) {
        UpdateResponse updateResponse = elasticsearchTemplate.update(entity);
        return updateResponse.getResult().ordinal() == 1;
    }

    /**
     * 局部更新
     *
     * @param entity 实体类
     * @return 执行记录数
     */
    public static <E> boolean patch(E entity) {
        Map<String, Object> map = BeanUtils.beanToMap(entity);
        String id = ReflectUtils.getFieldValue(entity, EntityConstants.SQL_ID).toString();
        UpdateQuery updateQuery = UpdateQuery.builder(id).withDocument(Document.from(map)).build();
        String indexName = getIndex(entity.getClass());
        UpdateResponse updateResponse = elasticsearchTemplate.update(updateQuery, IndexCoordinates.of(indexName));
        return updateResponse.getResult().ordinal() == 1;
    }

    /**
     * 局部更新
     *
     * @param id     主键
     * @param update 实体类
     * @param clazz  类
     */
    public static <E> boolean patch(String id, Map<String, Object> update, Class<E> clazz) {
        UpdateQuery updateQuery = UpdateQuery.builder(id).withDocument(Document.from(update)).build();
        String indexName = getIndex(clazz);
        UpdateResponse updateResponse = elasticsearchTemplate.update(updateQuery, IndexCoordinates.of(indexName));
        return updateResponse.getResult().ordinal() == 1;
    }

    /**
     * 批量全局修改
     *
     * @param list 实体集合
     */
    public static <E> void updateBatch(Collection<E> list) {
        patchBatch(list);
    }

    /**
     * 批量局部修改
     *
     * @param list 实体集合
     */
    public static <E> void patchBatch(Collection<E> list) {
        Class<?> targetClass = CollectionUtils.getFirst(list).getClass();
        String indexName = getIndex(targetClass);
        List<UpdateQuery> updateQueryList = CollectionUtils.newArrayList();
        for (E entity : list) {
            updateQueryList.add(UpdateQuery.builder(ReflectUtils.getFieldValue(entity, EntityConstants.SQL_ID).toString()).withDocument(Document.from(BeanUtils.beanToMap(entity))).build());
        }
        elasticsearchTemplate.bulkUpdate(updateQueryList, IndexCoordinates.of(indexName));
    }

    /**
     * 批量局部修改
     *
     * @param updateQueryList 对象Map
     * @param clazz           实体类
     */
    public static <E> void patchBatch(List<UpdateQuery> updateQueryList, Class<E> clazz) {
        String indexName = getIndex(clazz);
        elasticsearchTemplate.bulkUpdate(updateQueryList, IndexCoordinates.of(indexName));
    }

    /**
     * 根据主键ID删除对象
     *
     * @param id    主键
     * @param clazz 实体类
     */
    public static <E> void deleteById(String id, Class<E> clazz) {
        String indexName = getIndex(clazz);
        elasticsearchTemplate.delete(id, IndexCoordinates.of(indexName));
    }

    /**
     * 根据主键IDS批量删除对象
     *
     * @param ids   IDS
     * @param clazz 实体类
     */
    public static <E> long deleteByIds(Collection<String> ids, Class<E> clazz) {
        DeleteQuery deleteQuery = DeleteQuery.builder(Query.multiGetQuery(ids)).build();
        return elasticsearchTemplate.delete(deleteQuery, clazz).getDeleted();
    }

    /**
     * 删除对象
     *
     * @param query 查询条件
     * @param clazz 实体类
     * @return 执行记录数
     */
    public static <E> long delete(DeleteQuery query, Class<E> clazz) {
        return elasticsearchTemplate.delete(query, clazz).getDeleted();
    }

    /**
     * 根据id获取对象
     *
     * @param id    主键
     * @param clazz 实体类
     * @return 对象
     */
    public static <E> E getById(String id, Class<E> clazz) {
        return elasticsearchTemplate.get(id, clazz);
    }

    /**
     * 根据ids获取对象
     *
     * @param ids   主键集合
     * @param clazz 实体类
     * @return 集合对象
     */
    public static <E> List<E> getByIds(Collection<String> ids, Class<E> clazz) {
        Query query = Query.multiGetQuery(ids);
        SearchHits<E> searchHits = elasticsearchTemplate.search(query, clazz);
        if (searchHits.getTotalHits() == 0) {
            return List.of();
        }
        return searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    /**
     * 汇总条数
     *
     * @param query 查询条件
     * @param clazz 实体类
     * @return 查询笔数
     */
    public static <E> long count(Query query, Class<E> clazz) {
        return elasticsearchTemplate.count(query, clazz);
    }

    /**
     * 单个查询
     *
     * @param query 查询条件
     * @param clazz 实体类
     * @return 对象
     */
    public static <E> E selectOne(Query query, Class<E> clazz) {
        SearchHit<E> searchHit = elasticsearchTemplate.searchOne(query, clazz);
        return null == searchHit ? null : searchHit.getContent();
    }

    /**
     * 列表查询
     *
     * @param query 查询条件
     * @param clazz 实体类
     * @return 对象分页集合
     */
    public static <E> List<E> selectList(Query query, Class<E> clazz) {
        SearchHits<E> searchHits = elasticsearchTemplate.search(query, clazz);
        if (searchHits.getTotalHits() == 0) {
            return List.of();
        }
        return searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    /**
     * 列表查询
     *
     * @param clazz 实体类
     * @return 对象分页集合
     */
    public static <E> List<E> selectList(Class<E> clazz) {
        SearchHits<E> searchHits = elasticsearchTemplate.search(Query.findAll(), clazz);
        if (searchHits.getTotalHits() == 0) {
            return List.of();
        }
        return searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

}