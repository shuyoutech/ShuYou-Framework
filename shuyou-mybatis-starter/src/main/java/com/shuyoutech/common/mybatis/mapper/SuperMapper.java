package com.shuyoutech.common.mybatis.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;

import java.util.Collection;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-08-16 09:36
 **/
public interface SuperMapper<T> extends BaseMapper<T> {

    /**
     * 查询所有数据
     *
     * @return 数据集合
     */
    default List<T> selectList() {
        return this.selectList(new QueryWrapper<>());
    }

    /**
     * 批量插入
     *
     * @param entityList 对象集合
     * @return 是否成功
     */
    default boolean insertBatch(Collection<T> entityList) {
        return Db.saveBatch(entityList);
    }

    /**
     * 批量插入(包含限制条数)
     *
     * @param entityList 实体对象集合
     * @param batchSize  插入批次数量
     * @return 是否成功
     */
    default boolean insertBatch(Collection<T> entityList, int batchSize) {
        return Db.saveBatch(entityList, batchSize);
    }

    /**
     * 批量更新
     *
     * @param entityList 实体对象集合
     * @return 是否成功
     */
    default boolean updateBatchById(Collection<T> entityList) {
        return Db.updateBatchById(entityList);
    }

    /**
     * 批量更新(包含限制条数)
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     * @return 是否成功
     */
    default boolean updateBatchById(Collection<T> entityList, int batchSize) {
        return Db.updateBatchById(entityList, batchSize);
    }

    /**
     * 批量插入或更新
     *
     * @param entityList 实体对象集合
     * @return 是否成功
     */
    default boolean insertOrUpdateBatch(Collection<T> entityList) {
        return Db.saveOrUpdateBatch(entityList);
    }

    /**
     * 批量插入或更新(包含限制条数)
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     * @return 是否成功
     */
    default boolean insertOrUpdateBatch(Collection<T> entityList, int batchSize) {
        return Db.saveOrUpdateBatch(entityList, batchSize);
    }

    /**
     * 插入或更新(包含限制条数)
     *
     * @param entity 实体对象
     * @return 是否成功
     */
    default boolean insertOrUpdate(T entity) {
        return Db.saveOrUpdate(entity);
    }

}
