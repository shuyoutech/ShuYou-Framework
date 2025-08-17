package com.shuyoutech.common.mybatis.handler;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.shuyoutech.common.core.constant.EntityConstants;
import com.shuyoutech.common.satoken.util.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * @author YangChao
 * @date 2025-08-17 22:50
 **/
@Slf4j
public class InjectionMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入填充方法，用于在插入数据时自动填充实体对象中的创建时间、更新时间、创建人、更新人等信息
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        create(metaObject);
        update(metaObject);
    }

    /**
     * 更新填充方法，用于在更新数据时自动填充实体对象中的更新时间和更新人信息
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        update(metaObject);
    }

    private void create(MetaObject metaObject) {
        if (metaObject.hasGetter(EntityConstants.CREATE_TIME)) {
            Object createTime = metaObject.getValue(EntityConstants.CREATE_TIME);
            if (null == createTime) {
                this.strictInsertFill(metaObject, EntityConstants.CREATE_TIME, LocalDateTime::now, LocalDateTime.class);
            }
        }
        if (metaObject.hasGetter(EntityConstants.CREATE_USER_ID)) {
            Object createBy = metaObject.getValue(EntityConstants.CREATE_USER_ID);
            if (null == createBy) {
                this.setFieldValByName(EntityConstants.CREATE_USER_ID, AuthUtils.getLoginUserId(), metaObject);
            }
        }
        if (metaObject.hasGetter(EntityConstants.CREATE_ORG_ID)) {
            Object createOrg = metaObject.getValue(EntityConstants.CREATE_ORG_ID);
            if (null == createOrg) {
                this.setFieldValByName(EntityConstants.CREATE_ORG_ID, AuthUtils.getLoginOrgId(), metaObject);
            }
        }
        if (metaObject.hasGetter(EntityConstants.SQL_ID)) {
            Object id = metaObject.getValue(EntityConstants.SQL_ID);
            if (null == id) {
                TableId annotation = AnnotationUtil.getAnnotation(ReflectUtil.getField(metaObject.getOriginalObject().getClass(), EntityConstants.SQL_ID), TableId.class);
                IdType type = annotation.type();
                if (IdType.INPUT.equals(type)) {
                    this.setFieldValByName(EntityConstants.SQL_ID, IdUtil.getSnowflakeNextId(), metaObject);
                }
            }
        }
    }

    private void update(MetaObject metaObject) {
        if (metaObject.hasGetter(EntityConstants.UPDATE_TIME)) {
            Object updateTime = metaObject.getValue(EntityConstants.UPDATE_TIME);
            if (null == updateTime) {
                this.strictUpdateFill(metaObject, EntityConstants.UPDATE_TIME, LocalDateTime::now, LocalDateTime.class);
            }
        }
        if (metaObject.hasGetter(EntityConstants.UPDATE_USER_ID)) {
            Object updateBy = metaObject.getValue(EntityConstants.UPDATE_USER_ID);
            if (null == updateBy) {
                this.setFieldValByName(EntityConstants.UPDATE_USER_ID, AuthUtils.getLoginUserId(), metaObject);
            }
        }
    }

}
