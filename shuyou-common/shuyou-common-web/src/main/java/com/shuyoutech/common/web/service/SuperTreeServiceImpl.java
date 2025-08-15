package com.shuyoutech.common.web.service;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.shuyoutech.common.core.constant.EntityConstants;
import com.shuyoutech.common.core.constant.NumberConstants;
import com.shuyoutech.common.core.constant.StringConstants;
import com.shuyoutech.common.core.enums.QuerySortEnum;
import com.shuyoutech.common.core.exception.BusinessException;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import com.shuyoutech.common.core.util.*;
import com.shuyoutech.common.mongodb.MongoUtils;
import com.shuyoutech.common.mongodb.model.BaseEntity;
import com.shuyoutech.common.mongodb.model.BaseVo;
import com.shuyoutech.common.redis.constant.CacheConstants;
import com.shuyoutech.common.redis.util.RedisUtils;
import com.shuyoutech.common.satoken.util.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.shuyoutech.common.core.constant.EntityConstants.*;

/**
 * @author YangChao
 * @date 2025-07-08 09:39
 **/
@Slf4j
public class SuperTreeServiceImpl<Entity extends BaseEntity<Entity>, VO extends BaseVo> implements SuperTreeService<Entity, VO> {

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
        if (null == entity) {
            throw new BusinessException("entity is null");
        }
        buildTreeEntity(entity);
        buildCreate(entity);
        buildUpdate(entity);
        return MongoUtils.save(entity);
    }

    @Override
    public Collection<Entity> saveBatch(Collection<Entity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return null;
        }
        for (Entity entity : entityList) {
            buildTreeEntity(entity);
            buildCreate(entity);
            buildUpdate(entity);
        }
        return MongoUtils.saveBatch(entityList);
    }

    @Override
    public <UpdateVO> boolean patch(UpdateVO updateVO) {
        Entity entity = MapstructUtils.convert(updateVO, getEntityClass());
        if (null == entity) {
            throw new BusinessException("entity is null");
        }
        if (StringUtils.isBlank(entity.getId())) {
            throw new BusinessException("id is null");
        }
        buildUpdate(entity);
        return MongoUtils.patch(entity);
    }

    @Override
    public <UpdateVO> boolean update(UpdateVO updateVO) {
        Entity entity = MapstructUtils.convert(updateVO, getEntityClass());
        if (null == entity) {
            throw new BusinessException("entity is null");
        }
        if (StringUtils.isBlank(entity.getId())) {
            throw new BusinessException("id is null");
        }
        buildUpdate(entity);
        return MongoUtils.update(entity);
    }

    @Override
    public void updateBatch(Collection<Entity> entityList) {
        for (Entity entity : entityList) {
            buildUpdate(entity);
        }
        MongoUtils.updateBatch(entityList);
    }

    @Override
    public void patchBatch(Collection<Entity> entityList) {
        for (Entity entity : entityList) {
            buildUpdate(entity);
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
        return MapstructUtils.convert(entity, voClass);
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
            log.error("buildSort =============== sort or order error");
            return;
        }
        Sort.Direction direction;
        for (int i = 0; i < sortList.size(); ++i) {
            direction = QuerySortEnum.ASC.getValue().equalsIgnoreCase(orderList.get(i)) ? Sort.Direction.ASC : Sort.Direction.DESC;
            query.with(Sort.by(direction, sortList.get(i)));
        }
    }

    @Override
    public List<VO> buildTree(String parentId, List<VO> list) {
        return list.stream().filter(f -> parentId.equals(ReflectUtils.getFieldValue(f, PARENT_ID))).peek(m -> {
            List<VO> children = buildTree(m.getId(), list);
            ReflectUtils.setFieldValue(m, CHILDREN, children);
        }).collect(Collectors.toList());
    }

    protected void buildTreeEntity(Entity entity) {
        Object parentObj = ReflectUtils.getFieldValue(entity, PARENT_ID);
        String zero = String.valueOf(NumberConstants.ZERO);
        String parentId = (null == parentObj || StringUtils.isBlank(parentObj.toString())) ? zero : StringUtils.toString(parentObj);
        long treeId = RedisUtils.incr(CacheConstants.TREE_KEY + entityClass.getName());
        // 父节点为0
        String treePath;
        int treeLevel;
        if (zero.equals(parentId)) {
            treePath = zero + StringConstants.HYPHEN + treeId;
            treeLevel = 1;
        } else {
            Entity parent = MongoUtils.getById(parentId, entityClass);
            if (null == parent) {
                throw new BusinessException(StringUtils.format("parentId:{} 无此记录", parentId));
            }
            treePath = ReflectUtils.getFieldValue(parent, TREE_PATH) + StringConstants.HYPHEN + treeId;
            treeLevel = Integer.parseInt(ReflectUtils.getFieldValue(parent, TREE_LEVEL).toString()) + 1;
        }
        entity.setId(String.valueOf(treeId));
        ReflectUtils.setFieldValue(entity, PARENT_ID, parentId);
        ReflectUtils.setFieldValue(entity, TREE_PATH, treePath);
        ReflectUtils.setFieldValue(entity, TREE_LEVEL, treeLevel);
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
