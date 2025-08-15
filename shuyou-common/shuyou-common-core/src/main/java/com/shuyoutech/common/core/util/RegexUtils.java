package com.shuyoutech.common.core.util;

import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-06 15:02
 **/
public class RegexUtils extends cn.hutool.core.util.ReUtil {

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     *
     * @param content  指定字符串
     * @param patterns 需要检查的字符串数组
     * @return boolean
     */
    public static boolean matches(String content, List<String> patterns) {
        if (StringUtils.isBlank(content) || CollectionUtils.isEmpty(patterns)) {
            return false;
        }
        for (String pattern : patterns) {
            if (isMatch(pattern, content)) {
                return true;
            }
        }
        return false;
    }

}
