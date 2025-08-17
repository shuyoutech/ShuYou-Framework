package com.shuyoutech.common.core.util;

import cn.hutool.core.util.PageUtil;
import com.shuyoutech.common.core.constant.NumberConstants;
import com.shuyoutech.common.core.service.BatchHandler;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author YangChao
 * @date 2025-07-06 14:43
 **/
public class CollectionUtils extends cn.hutool.core.collection.CollectionUtil {

    /**
     * 将list按limit大小等分，最后多余的单独一份
     *
     * @param source 集合
     * @return 分批集合
     */
    public static <T> List<List<T>> batch(Collection<T> source) {
        return batch(source, NumberConstants.ONE_THOUSAND);
    }

    /**
     * 将list按limit大小等分，最后多余的单独一份
     *
     * @param source 集合对象
     * @param limit  大小等分
     * @return 等分集合对象
     */
    public static <T> List<List<T>> batch(Collection<T> source, int limit) {
        List<List<T>> result = CollectionUtils.newArrayList();

        if (CollectionUtils.isEmpty(source)) {
            return result;
        }

        int size = source.size();
        if (size <= limit) {
            result.add(newArrayList(source));
            return result;
        }

        // 先计算出余数
        int remain = size % limit;
        // 分批数
        int batchSize = size / limit;

        for (int i = 0; i < batchSize; i++) {
            int fromIndex = i * limit;
            int toIndex = fromIndex + limit;
            result.add(sub(source, fromIndex, toIndex));
        }

        if (remain > 0) {
            result.add(sub(source, size - remain, size));
        }

        return result;
    }

    /**
     * 将一组数据平均分成n组
     *
     * @param source 集合对象
     * @param n      分成n组
     * @return 平均分成n组集合对象
     */
    public static <T> List<List<T>> average(Collection<T> source, int n) {
        List<List<T>> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(source) || n <= 0) {
            return result;
        }
        // 先计算出余数
        int remainder = source.size() % n;
        // 然后是商
        int number = source.size() / n;
        for (int i = 0; i < n; i++) {
            result.add(sub(source, i * number, (i + 1) * number));
        }
        if (remainder > 0) {
            result.add(sub(source, number * n, source.size()));
        }
        return result;
    }

    /**
     * 针对集合数据，进行1000一组批次处理数据
     *
     * @param list    数据集合
     * @param handler 批次处理器
     */
    public static <T> void handle(Collection<T> list, BatchHandler handler) {
        if (isEmpty(list)) {
            return;
        }
        int totalCount = list.size();
        int pageSize = NumberConstants.ONE_THOUSAND;
        if (totalCount <= pageSize) {
            handler.handle(list);
            return;
        }
        int totalPage = PageUtil.totalPage(totalCount, pageSize);
        List<T> subList;
        for (int i = 0; i < totalPage; i++) {
            subList = CollectionUtils.sub(list, PageUtil.getStart(i, pageSize), PageUtil.getEnd(i, pageSize));
            handler.handle(subList);
        }
    }

    public static List<String> translate(Collection<String> list, Map<String, String> dictMap) {
        List<String> result = CollectionUtils.newArrayList();
        if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(dictMap)) {
            return result;
        }
        for (String value : CollectionUtils.newHashSet(list)) {
            result.add(dictMap.getOrDefault(value, value));
        }
        return result;
    }

}
