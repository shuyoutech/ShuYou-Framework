package com.shuyoutech.common.web.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.shuyoutech.common.core.constant.EntityConstants;
import com.shuyoutech.common.core.constant.StringConstants;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.util.*;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.mongodb.model.BaseEntity;
import com.shuyoutech.common.mongodb.model.BaseVo;
import com.shuyoutech.common.satoken.util.AuthUtils;
import com.shuyoutech.common.web.enums.QuerySortEnum;
import com.shuyoutech.common.web.model.PageQuery;
import com.shuyoutech.common.web.model.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author YangChao
 * @since 2025-07-07 20:30
 **/
@Slf4j
public class SuperServiceImpl<Entity extends BaseEntity<Entity>, VO extends BaseVo> implements SuperService<Entity, VO> {

    protected Class<Entity> entityClass = getEntityClass();
    protected Class<VO> voClass = getVoClass();

    @Override
    @SuppressWarnings("unchecked")
    public Class<Entity> getEntityClass() {
        return (Class<Entity>) TypeUtil.getTypeArgument(this.getClass(), 0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<VO> getVoClass() {
        return (Class<VO>) TypeUtil.getTypeArgument(this.getClass(), 1);
    }

    @Override
    public <SaveVO> Entity save(SaveVO saveVO) {
        Entity entity = MapstructUtils.convert(saveVO, getEntityClass());
        buildCreate(entity);
        buildUpdate(entity);
        return MongoUtils.save(entity);
    }

    @Override
    public <SaveVO> Collection<Entity> saveBatch(Collection<SaveVO> entityList) {
        Collection<Entity> list = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(entityList)) {
            return list;
        }
        Entity entity;
        for (SaveVO save : entityList) {
            entity = MapstructUtils.convert(save, getEntityClass());
            buildCreate(entity);
            buildUpdate(entity);
            list.add(entity);
        }
        return MongoUtils.saveBatch(list);
    }

    @Override
    public <UpdateVO> boolean patch(UpdateVO updateVO) {
        Entity entity = MapstructUtils.convert(updateVO, getEntityClass());
        if (null == entity) {
            throw new BusinessException("patch entity is null");
        }
        if (StringUtils.isBlank(entity.getId())) {
            throw new BusinessException("patch id is null");
        }
        buildUpdate(entity);
        return MongoUtils.patch(entity);
    }

    @Override
    public <UpdateVO> boolean update(UpdateVO updateVO) {
        Entity entity = MapstructUtils.convert(updateVO, getEntityClass());
        if (null == entity) {
            throw new BusinessException("update entity is null");
        }
        if (StringUtils.isBlank(entity.getId())) {
            throw new BusinessException("update id is null");
        }
        buildUpdate(entity);
        return MongoUtils.update(entity);
    }

    @Override
    public <UpdateVO> void updateBatch(Collection<UpdateVO> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return;
        }
        Collection<Entity> list = CollectionUtils.newArrayList();
        Entity entity;
        for (UpdateVO update : entityList) {
            entity = MapstructUtils.convert(update, getEntityClass());
            buildUpdate(entity);
            list.add(entity);
        }
        MongoUtils.updateBatch(list);
    }

    @Override
    public <UpdateVO> void patchBatch(Collection<UpdateVO> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return;
        }
        Collection<Entity> list = CollectionUtils.newArrayList();
        Entity entity;
        for (UpdateVO update : entityList) {
            entity = MapstructUtils.convert(update, getEntityClass());
            buildUpdate(entity);
            list.add(entity);
        }
        MongoUtils.patchBatch(entityList);
    }

    @Override
    public boolean deleteById(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        return MongoUtils.deleteById(id, entityClass) > 0;
    }

    @Override
    public boolean deleteByIds(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        return MongoUtils.deleteByIds(ids, entityClass) > 0;
    }

    @Override
    public Entity getById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return MongoUtils.getById(id, entityClass);
    }

    @Override
    public List<Entity> getByIds(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        return MongoUtils.getByIds(ids, entityClass);
    }

    @Override
    public <K> Map<K, Entity> getByIds(Collection<String> ids, Function<Entity, K> keyMapper) {
        return getByIds(ids, keyMapper, Function.identity());
    }

    @Override
    public <K, V> Map<K, V> getByIds(Collection<String> ids, Function<Entity, K> keyMapper, Function<Entity, V> valueMapper) {
        Map<K, V> map = MapUtils.newHashMap();
        if (CollectionUtils.isEmpty(ids)) {
            return map;
        }
        List<Entity> list = MongoUtils.getByIds(ids, entityClass);
        if (CollectionUtils.isEmpty(list)) {
            return map;
        }
        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper, (key1, key2) -> key2));
    }

    @Override
    public <QueryVO> Query buildQuery(QueryVO queryVO) {
        return new Query();
    }

    @Override
    public long count(Query query) {
        return MongoUtils.count(query, entityClass);
    }

    @Override
    public Entity selectOne(Query query) {
        return MongoUtils.selectOne(query, entityClass);
    }

    @Override
    public PageResult<Entity> selectPage(PageQuery<Query> pageQuery) {
        PageResult<Entity> pageResult = PageResult.empty();
        Query query = pageQuery.getQuery();
        long count = MongoUtils.count(query, entityClass);
        if (0 == count) {
            return pageResult;
        }
        Pageable pageable = PageRequest.of(pageQuery.getPageNum() - 1, pageQuery.getPageSize());
        query.with(pageable);
        buildSort(query, pageQuery.getSort(), pageQuery.getOrder());
        pageResult.setRows(MongoUtils.selectList(query, entityClass));
        pageResult.setTotal(count);
        return pageResult;
    }

    @Override
    public PageResult<VO> selectPageVo(PageQuery<Query> pageQuery) {
        PageResult<VO> pageResultVo = PageResult.empty();
        PageResult<Entity> pageResult = selectPage(pageQuery);
        if (null == pageResult || 0 == pageResult.getTotal() || CollectionUtils.isEmpty(pageResult.getRows())) {
            return pageResultVo;
        }
        pageResultVo.setRows(this.convertTo(pageResult.getRows()));
        pageResultVo.setTotal(pageResult.getTotal());
        return pageResultVo;
    }

    @Override
    public List<Entity> selectList() {
        return MongoUtils.selectList(entityClass);
    }

    @Override
    public List<Entity> selectList(Query query) {
        return MongoUtils.selectList(query, entityClass);
    }

    @Override
    public List<VO> selectListVo(Query query) {
        List<Entity> list = MongoUtils.selectList(query, entityClass);
        return this.convertTo(list);
    }

    @Override
    public List<VO> convertTo(List<Entity> list) {
        return MapstructUtils.convert(list, voClass);
    }

    @Override
    public VO convertTo(Entity entity) {
        if (null == entity) {
            return null;
        }
        return convertTo(Collections.singletonList(entity)).getFirst();
    }

    @Override
    public Entity modelToEntity(VO vo) {
        return MapstructUtils.convert(vo, entityClass);
    }

    protected void buildSort(Query query, String sort, String order) {
        if (StringUtils.isBlank(sort) || StringUtils.isBlank(order)) {
            return;
        }
        List<String> sortList = StringUtils.split(sort, StringConstants.COMMA);
        List<String> orderList = StringUtils.split(order, StringConstants.COMMA);
        if (sortList.size() != orderList.size()) {
            log.error("buildSort sort or order size different");
            return;
        }
        Sort.Direction direction;
        for (int i = 0; i < sortList.size(); ++i) {
            direction = QuerySortEnum.ASC.getValue().equalsIgnoreCase(orderList.get(i)) ? Sort.Direction.ASC : Sort.Direction.DESC;
            query.with(Sort.by(direction, sortList.get(i)));
        }
    }

    private void buildCreate(Entity entity) {
        if (ReflectUtil.hasField(entityClass, EntityConstants.CREATE_TIME)) {
            Object createTime = ReflectUtil.getFieldValue(entity, EntityConstants.CREATE_TIME);
            if (null == createTime) {
                ReflectUtil.setFieldValue(entity, EntityConstants.CREATE_TIME, new Date());
            }
        }
        if (ReflectUtil.hasField(entityClass, EntityConstants.CREATE_USER_ID)) {
            Object createUserId = ReflectUtil.getFieldValue(entity, EntityConstants.CREATE_USER_ID);
            if (ObjectUtils.isEmpty(createUserId)) {
                ReflectUtil.setFieldValue(entity, EntityConstants.CREATE_USER_ID, AuthUtils.getLoginUserId());
            }
        }
        if (ReflectUtil.hasField(entityClass, EntityConstants.CREATE_ORG_ID)) {
            Object createOrgId = ReflectUtil.getFieldValue(entity, EntityConstants.CREATE_ORG_ID);
            if (ObjectUtils.isEmpty(createOrgId)) {
                ReflectUtil.setFieldValue(entity, EntityConstants.CREATE_ORG_ID, AuthUtils.getLoginOrgId());
            }
        }
        if (ReflectUtil.hasField(entityClass, EntityConstants.SQL_ID)) {
            Object id = ReflectUtil.getFieldValue(entity, EntityConstants.SQL_ID);
            if (ObjectUtils.isEmpty(id)) {
                ReflectUtil.setFieldValue(entity, EntityConstants.SQL_ID, IdUtil.getSnowflakeNextIdStr());
            }
        }
    }

    private void buildUpdate(Entity entity) {
        if (ReflectUtil.hasField(entityClass, EntityConstants.UPDATE_TIME)) {
            Object updateTime = ReflectUtil.getFieldValue(entity, EntityConstants.UPDATE_TIME);
            if (null == updateTime) {
                ReflectUtil.setFieldValue(entity, EntityConstants.UPDATE_TIME, new Date());
            }
        }
        if (ReflectUtil.hasField(entityClass, EntityConstants.UPDATE_USER_ID)) {
            Object updateUserId = ReflectUtil.getFieldValue(entity, EntityConstants.UPDATE_USER_ID);
            if (ObjectUtils.isEmpty(updateUserId)) {
                ReflectUtil.setFieldValue(entity, EntityConstants.UPDATE_USER_ID, AuthUtils.getLoginUserId());
            }
        }
    }

}
