package com.shuyoutech.common.core.util;

import cn.hutool.core.util.StrUtil;
import com.shuyoutech.common.core.constant.StringConstants;

/**
 * @author YangChao
 * @date 2025-07-06 12:11
 **/
public class StringUtils extends StrUtil {

    /**
     * 拼接字符串
     *
     * @param charSequences 字符串
     * @return String
     */
    public static String build(CharSequence... charSequences) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence charSequence : charSequences) {
            sb.append(charSequence);
        }
        return sb.toString();
    }

    /**
     * 拼接路径
     *
     * @param paths 路径
     * @return String
     */
    public static String buildPath(String... paths) {
        StringBuilder sb = new StringBuilder();
        int length = paths.length;
        String path;
        for (int i = 0; i < length; i++) {
            path = paths[i];
            if (isBlank(path)) {
                continue;
            }
            if (i == length - 1) {
                if (path.endsWith(StringConstants.SLASH)) {
                    path = path.substring(0, path.indexOf(StringConstants.SLASH));
                }
            }
            sb.append(path);
            if (i <= length - 1) {
                if (!path.endsWith(StringConstants.SLASH)) {
                    sb.append("/");
                }
            }
        }
        return sb.toString();
    }

}
