package com.shuyoutech.common.web.service;

import com.shuyoutech.common.mongodb.model.TreeEntity;
import com.shuyoutech.common.mongodb.model.TreeVo;
import com.shuyoutech.common.web.model.PageQuery;
import com.shuyoutech.common.web.model.PageResult;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author YangChao
 * @since 2025-07-08 09:37
 **/
public interface SuperTreeService<Entity extends TreeEntity<Entity>, VO extends TreeVo> {

    Class<Entity> getEntityClass();

    Class<VO> getVoClass();

    <SaveVO> Entity save(SaveVO saveVO);

    <SaveVO> Collection<Entity> saveBatch(Collection<SaveVO> entityList);

    <UpdateVO> boolean patch(UpdateVO updateVO);

    <UpdateVO> boolean update(UpdateVO updateVO);

    <UpdateVO> void updateBatch(Collection<UpdateVO> entityList);

    <UpdateVO> void patchBatch(Collection<UpdateVO> entityList);

    boolean deleteById(Long id);

    boolean deleteByIds(Collection<Long> ids);

    Entity getById(Long id);

    List<Entity> getByIds(Collection<Long> ids);

    <K> Map<K, Entity> getByIds(Collection<Long> ids, Function<Entity, K> keyMapper);

    <K, V> Map<K, V> getByIds(Collection<Long> ids, Function<Entity, K> keyMapper, Function<Entity, V> valueMapper);

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

    List<VO> buildTree(Long parentId, List<VO> list);

}
