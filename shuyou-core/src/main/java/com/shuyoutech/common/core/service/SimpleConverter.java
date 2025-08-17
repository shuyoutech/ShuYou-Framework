package com.shuyoutech.common.core.service;

import com.shuyoutech.common.core.util.ConvertUtils;

/**
 * @author YangChao
 * @date 2025-07-06 12:15
 **/
public class SimpleConverter  implements org.springframework.cglib.core.Converter {

    /**
     * 类型转换
     *
     * @param value   对象属性值
     * @param target  对象对应类
     * @param context 对象属性对应set方法名,eg.setId
     * @return 转换对象
     */
    @Override
    public Object convert(Object value, Class target, Object context) {
        return ConvertUtils.convert(value, target);
    }

}
