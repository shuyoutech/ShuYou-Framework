package com.shuyoutech.common.core.util;

import cn.hutool.core.util.StrUtil;
import com.shuyoutech.common.core.constant.StringConstants;

import static cn.hutool.core.util.ReUtil.RE_CHINESES;

/**
 * @author YangChao
 * @since 2025-07-06 12:11
 **/
public class StringUtils extends StrUtil {

    /**
     * 计算文本长度,汉字：2字符 其余都是1
     *
     * @param text 文本
     * @return 长度
     */
    public static int calTextLength(String text) {
        if (StringUtils.isEmpty(text)) {
            return 0;
        }
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (RegexUtils.isMatch(RE_CHINESES, String.valueOf(c))) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length;
    }

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
