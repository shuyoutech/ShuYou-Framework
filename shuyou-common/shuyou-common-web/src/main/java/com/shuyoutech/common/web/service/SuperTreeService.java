package com.shuyoutech.common.web.service;

import com.shuyoutech.common.mongodb.model.BaseEntity;
import com.shuyoutech.common.mongodb.model.BaseVo;
import com.shuyoutech.common.core.model.PageQuery;
import com.shuyoutech.common.core.model.PageResult;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author YangChao
 * @date 2025-07-08 09:37
 **/
public interface SuperTreeService<Entity extends BaseEntity<Entity>, VO extends BaseVo> {

    Class<Entity> getEntityClass();

    Class<VO> getVoClass();

    <SaveVO> Entity save(SaveVO saveVO);

    Collection<Entity> saveBatch(Collection<Entity> entityList);

    <UpdateVO> boolean patch(UpdateVO updateVO);

    <UpdateVO> boolean update(UpdateVO updateVO);

    void updateBatch(Collection<Entity> entityList);

    void patchBatch(Collection<Entity> entityList);

    boolean deleteById(String id);

    boolean deleteByIds(Collection<String> ids);

    Entity getById(String id);

    List<Entity> getByIds(Collection<String> ids);

    <K> Map<K, Entity> getByIds(Collection<String> ids, Function<Entity, K> keyMapper);

    <K, V> Map<K, V> getByIds(Collection<String> ids, Function<Entity, K> keyMapper, Function<Entity, V> valueMapper);

    <QueryVO> Query buildQuery(QueryVO queryVO);

    long count(Query query);

    Entity selectOne(Query query);

    PageResult<Entity> selectPage(PageQuery<Query> pageQuery);

    PageResult<VO> selectPageVo(PageQuery<Query> pageQuery);

    List<Entity> selectList();

    List<Entity> selectList(Query query);

    List<VO> selectListVo(Query query);

    List<VO> convertTo(List<Entity> list);

    VO convertTo(Entity entity);

    Entity modelToEntity(VO vo);

    List<VO> buildTree(String parentId, List<VO> list);

}
