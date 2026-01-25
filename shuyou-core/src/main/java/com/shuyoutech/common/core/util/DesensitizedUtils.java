package com.shuyoutech.common.core.util;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author YangChao
 * @since 2025-09-18 14:03
 **/
public class DesensitizedUtils extends DesensitizedUtil {

    /**
     * 显示首尾API KEY
     *
     * @param apiKey 密钥
     * @return 脱敏后的 key
     */
    public static String apiKey(String apiKey) {
        if (StringUtils.isBlank(apiKey)) {
            return StrUtil.EMPTY;
        }
        int length = apiKey.length();
        if (length < 10) {
            return "********************";
        }
        return apiKey.substring(0, 7) + "**********" + apiKey.substring(length - 3);
    }

}
