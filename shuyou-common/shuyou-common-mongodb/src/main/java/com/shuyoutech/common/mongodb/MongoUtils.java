package com.shuyoutech.common.mongodb;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ReflectUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.shuyoutech.common.core.constant.EntityConstants;
import com.shuyoutech.common.core.constant.NumberConstants;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.mongodb.model.BaseEntity;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.service.AbstractRunnable;
import com.shuyoutech.common.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.data.util.Pair;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author YangChao
 * @date 2025-04-06 20:26
 **/
@Slf4j
public class MongoUtils {

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNamePrefix("mongodb-pool-%d").build();

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(NumberConstants.EIGHT, NumberConstants.EIGHT, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), THREAD_FACTORY);

    public static final MongoTemplate mongoTemplate = SpringUtils.getBean("mongoTemplate");

    /**
     * 构建分页对象
     *
     * @param pageQuery 查询对象
     * @return 分页对象
     */
    public static <T> PageQuery<Query> buildPageQuery(PageQuery<T> pageQuery) {
        PageQuery<Query> page = new PageQuery<>();
        page.setPageNum(pageQuery.getPageNum());
        page.setPageSize(pageQuery.getPageSize());
        page.setSort(pageQuery.getSort());
        page.setOrder(pageQuery.getOrder());
        return page;
    }

    /**
     * 插入数据 如果已存在则报错
     *
     * @param entity 实体类
     * @return 执行记录数
     */
    public static <E extends BaseEntity<E>> E insert(E entity) {
        return mongoTemplate.insert(entity);
    }

    /**
     * 批量插入数据
     *
     * @param list 实体集合
     * @return 执行记录数
     */
    public static <E extends BaseEntity<E>> Collection<E> insertBatch(Collection<E> list) {
        return mongoTemplate.insertAll(list);
    }

    /**
     * 保存对象,存在则更新，不存在则插入
     *
     * @param entity 实体类
     * @return 执行记录数
     */
    public static <E extends BaseEntity<E>> E save(E entity) {
        return mongoTemplate.save(entity);
    }

    /**
     * 批量保存对象 已存在的删除在插入
     *
     * @param list 实体集合
     * @return 执行记录数
     */
    public static <E extends BaseEntity<E>> Collection<E> saveBatch(Collection<E> list) {
        try {
            return mongoTemplate.insertAll(list);
        } catch (DuplicateKeyException duplicateKeyException) {
            for (E e : list) {
                save(e);
            }
        }
        return list;
    }

    /**
     * 更新全部字段
     *
     * @param entity 实体类
     * @return 执行记录数
     */
    public static <E extends BaseEntity<E>> boolean update(E entity) {
        Update update = entityToUpdate(entity, false);
        if (null == update) {
            return false;
        }
        String id = entity.getId();
        return patch(id, update, entity.getClass());
    }

    /**
     * 局部更新
     *
     * @param entity 实体类
     * @return 执行记录数
     */
    public static <E extends BaseEntity<E>> boolean patch(E entity) {
        Update update = entityToUpdate(entity, true);
        if (null == update) {
            return false;
        }
        String id = entity.getId();
        return patch(id, update, entity.getClass());
    }

    /**
     * 局部更新
     *
     * @param id     主键
     * @param update 实体类
     * @param clazz  类
     */
    public static <E extends BaseEntity<E>> boolean patch(String id, Update update, Class<E> clazz) {
        Query query = Query.query(Criteria.where(EntityConstants.NOSQL_ID).is(id));
        UpdateResult result = mongoTemplate.upsert(query, update, clazz);
        return result.getModifiedCount() > 0;
    }

    /**
     * 批量全局修改
     *
     * @param list 实体集合
     */
    public static <E extends BaseEntity<E>> void updateBatch(Collection<E> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Class<?> targetClass = list.iterator().next().getClass();
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, targetClass);
        Update update;
        Query query;
        for (E e : list) {
            query = Query.query(Criteria.where(EntityConstants.NOSQL_ID).is(e.getId()));
            update = entityToUpdate(e, false);
            if (null == update) {
                continue;
            }
            ops.updateOne(query, update);
        }
        ops.execute();
    }

    /**
     * 批量局部修改
     *
     * @param list 实体集合
     */
    public static <E extends BaseEntity<E>> void patchBatch(Collection<E> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Class<?> targetClass = list.iterator().next().getClass();
        Update update;
        Query query;
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, targetClass);
        for (E e : list) {
            query = Query.query(Criteria.where(EntityConstants.NOSQL_ID).is(e.getId()));
            update = entityToUpdate(e, true);
            if (null == update) {
                continue;
            }
            ops.updateOne(query, update);
        }
        ops.execute();
    }

    /**
     * 批量局部修改
     *
     * @param updateMap 对象Map
     * @param clazz     实体类
     */
    public static <E extends BaseEntity<E>> void patchBatch(Map<String, Update> updateMap, Class<E> clazz) {
        if (MapUtils.isEmpty(updateMap)) {
            return;
        }
        try {
            BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, clazz);
            List<Pair<Query, UpdateDefinition>> updates = CollectionUtils.newArrayList();
            for (String id : updateMap.keySet()) {
                updates.add(Pair.of(Query.query(Criteria.where(EntityConstants.NOSQL_ID).is(id)), updateMap.get(id)));
            }
            ops.updateMulti(updates);
            ops.execute();
        } catch (Exception e) {
            log.error("patchBatch ========================== exception : {}", e.getMessage());
        }
    }

    public static <E extends BaseEntity<E>> Update entityToUpdate(E entity, boolean beenPatch) {
        Dict dict = Dict.parse(entity);
        if (null == dict) {
            throw new BusinessException("dict is null");
        }
        Update update = null;
        for (String key : dict.keySet()) {
            if (EntityConstants.SQL_ID.equals(key)) {
                continue;
            }
            if (beenPatch && ObjectUtils.isEmpty(dict.get(key))) {
                continue;
            }
            if (null == update) {
                update = new Update();
            }
            update.set(key, dict.get(key));
        }
        return update;
    }

    /**
     * 根据主键ID删除对象
     *
     * @param id    主键
     * @param clazz 实体类
     * @return 执行记录数
     */
    public static <E extends BaseEntity<E>> long deleteById(Serializable id, Class<E> clazz) {
        Query query = new Query(Criteria.where(EntityConstants.NOSQL_ID).is(id));
        DeleteResult deleteResult = mongoTemplate.remove(query, clazz);
        return deleteResult.getDeletedCount();
    }

    /**
     * 根据主键IDS批量删除对象
     *
     * @param ids   IDS
     * @param clazz 实体类
     * @return 执行记录数
     */
    public static <E extends BaseEntity<E>> long deleteByIds(Collection<?> ids, Class<E> clazz) {
        Query query = new Query(Criteria.where(EntityConstants.NOSQL_ID).in(ids));
        DeleteResult deleteResult = mongoTemplate.remove(query, clazz);
        return deleteResult.getDeletedCount();
    }

    /**
     * 删除对象
     *
     * @param query 查询条件
     * @param clazz 实体类
     * @return 执行记录数
     */
    public static <E extends BaseEntity<E>> long delete(Query query, Class<E> clazz) {
        DeleteResult deleteResult = mongoTemplate.remove(query, clazz);
        return deleteResult.getDeletedCount();
    }

    /**
     * 根据id获取对象
     *
     * @param id    主键
     * @param clazz 实体类
     * @return 对象
     */
    public static <E extends BaseEntity<E>> E getById(Serializable id, Class<E> clazz) {
        return mongoTemplate.findById(id, clazz);
    }

    /**
     * 根据ids获取对象
     *
     * @param idList 主键集合
     * @param clazz  实体类
     * @return 集合对象
     */
    public static <E extends BaseEntity<E>> List<E> getByIds(Collection<? extends Serializable> idList, Class<E> clazz) {
        if (CollectionUtils.isEmpty(idList)) {
            return CollectionUtils.newArrayList();
        }
        Query query = new Query(Criteria.where(EntityConstants.NOSQL_ID).in(idList));
        List<E> list = mongoTemplate.find(query, clazz);
        if (CollectionUtils.isEmpty(list)) {
            return CollectionUtils.newArrayList();
        }
        return list;
    }

    /**
     * 获取实体表名称
     *
     * @param clazz 实体类
     * @return 类名
     */
    private static <E extends BaseEntity<E>> String getCollection(Class<E> clazz) {
        org.springframework.data.mongodb.core.mapping.Document annotation = AnnotationUtil.getAnnotation(clazz, org.springframework.data.mongodb.core.mapping.Document.class);
        String collection = annotation.collection();
        if (StringUtils.isEmpty(collection)) {
            throw new BusinessException("请实体类上面配置collection");
        } else {
            return collection;
        }
    }

    /**
     * 汇总条数
     *
     * @param query 查询条件
     * @param clazz 实体类
     * @return 查询笔数
     */
    public static <E extends BaseEntity<E>> long count(Query query, Class<E> clazz) {
        return mongoTemplate.count(query, clazz);
    }

    /**
     * 单个查询
     *
     * @param query 查询条件
     * @param clazz 实体类
     * @return 对象
     */
    public static <E extends BaseEntity<E>> E selectOne(Query query, Class<E> clazz) {
        return mongoTemplate.findOne(query, clazz);
    }

    /**
     * 列表查询
     *
     * @param query 查询条件
     * @param clazz 实体类
     * @return 对象分页集合
     */
    public static <E extends BaseEntity<E>> List<E> selectList(Query query, Class<E> clazz) {
        return mongoTemplate.find(query, clazz);
    }

    /**
     * 列表查询
     *
     * @param clazz 实体类
     * @return 对象分页集合
     */
    public static <E extends BaseEntity<E>> List<E> selectList(Class<E> clazz) {
        Query query = new Query();
        return mongoTemplate.find(query, clazz);
    }

    /**
     * 滚动查询所有数据，注意内存溢出
     *
     * @param query 查询条件
     * @param clazz 实体类
     * @return 对象集合
     */
    public static <E extends BaseEntity<E>> List<E> selectScroll(Query query, Class<E> clazz) {
        List<E> result = CollectionUtils.newArrayList();
        String collection = getCollection(clazz);
        Bson bson = new BasicDBObject(query.getQueryObject());
        FindIterable<Document> documents = mongoTemplate.getCollection(collection).find(bson);
        if (query.getLimit() > 0) {
            documents.limit(query.getLimit());
        }
        documents.sort(query.getSortObject());
        documents.batchSize(NumberConstants.FIVE_THOUSAND);
        MongoCursor<Document> iterator = documents.iterator();
        Document d;
        E e;
        while (iterator.hasNext()) {
            d = iterator.next();
            e = MapstructUtils.convert(d, clazz);
            if (null != e) {
                ReflectUtil.setFieldValue(e, EntityConstants.SQL_ID, d.get(EntityConstants.NOSQL_ID).toString());
                result.add(e);
            }
        }
        return result;
    }

    /**
     * 滚动查询所有数据，注意内存溢出
     *
     * @param query         查询条件
     * @param clazz         实体类
     * @param runnableClass 处理类
     */
    public static <E extends BaseEntity<E>, R extends AbstractRunnable<E>> void selectScroll(Query query, Class<E> clazz, Class<R> runnableClass) {
        long count = count(query, clazz);
        if (count == 0) {
            return;
        }
        String collection = getCollection(clazz);
        Bson bson = new BasicDBObject(query.getQueryObject());
        FindIterable<Document> documents = mongoTemplate.getCollection(collection).find(bson);
        documents.batchSize(NumberConstants.FIVE_THOUSAND);
        documents.sort(query.getSortObject());
        MongoCursor<Document> iterator = documents.iterator();
        Document d;
        E e;
        int i = 0;
        List<E> list = CollectionUtils.newArrayList();
        int sleepTime;
        int limit = NumberConstants.ONE_THOUSAND;
        while (iterator.hasNext()) {
            d = iterator.next();
            e = MapstructUtils.convert(d, clazz);
            if (null == e) {
                continue;
            }
            ReflectUtil.setFieldValue(e, EntityConstants.SQL_ID, d.get(EntityConstants.NOSQL_ID).toString());
            list.add(e);
            ++i;
            if (0 == i % limit) {
                sleepTime = 0;
                while (executor.getQueue().size() > NumberConstants.SIXTY_FOUR) {
                    if (sleepTime >= 300) {
                        log.error("selectScroll ========================= threads : {}, runnable : {}, 睡了5min，请优化处理程序避免后续查询中断!", executor.getQueue().size(), runnableClass.getSimpleName());
                    }
                    ThreadUtil.sleep(1000);
                    sleepTime++;
                }
                R runnable = ReflectUtil.newInstance(runnableClass);
                runnable.setTotal(count);
                runnable.setList(list);
                runnable.setStart(i - limit + 1);
                runnable.setEnd(i);
                executor.execute(runnable);
                list = CollectionUtils.newArrayList();
            }
        }
        if (CollectionUtils.isNotEmpty(list)) {
            R runnable = ReflectUtil.newInstance(runnableClass);
            runnable.setTotal(count);
            runnable.setList(list);
            runnable.setStart(i - list.size() + 1);
            runnable.setEnd(i);
            executor.execute(runnable);
        }
    }

    /**
     * 聚合操作
     *
     * @param aggregation 条件
     * @param clazz       实体类
     * @return 实体集合
     */
    public static <E extends BaseEntity<E>> List<Document> aggregate(Aggregation aggregation, Class<E> clazz) {
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, clazz, Document.class);
        return results.getMappedResults();
    }

    /**
     * 聚合操作
     *
     * @param criteria       条件
     * @param groupOperation 分组操作
     * @param clazz          实体类
     * @return 实体集合
     */
    public static <E extends BaseEntity<E>> List<Document> aggregate(Criteria criteria, GroupOperation groupOperation, Class<E> clazz) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), groupOperation);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, clazz, Document.class);
        return results.getMappedResults();
    }

}
