package com.shuyoutech.common.core.util;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.parser.NodeParser;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-08 15:37
 **/
public class TreeUtils extends cn.hutool.core.lang.tree.TreeUtil {

    /**
     * 根据前端定制差异化字段
     */
    public static final TreeNodeConfig DEFAULT_CONFIG = TreeNodeConfig.DEFAULT_CONFIG.setNameKey("label");

    /**
     * 构建树
     *
     * @param list       源数据集合
     * @param nodeParser 转换器
     * @return Tree
     */
    public static <T, E> List<Tree<E>> build(List<T> list, E rootId, NodeParser<T, E> nodeParser) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return TreeUtils.build(list, rootId, DEFAULT_CONFIG, nodeParser);
    }

}
