package com.shuyoutech.common.core.util;

import cn.hutool.core.util.NumberUtil;

/**
 * @author YangChao
 * @date 2025-07-06 14:51
 **/
public class NumberUtils extends NumberUtil {

    /**
     * 百分比 = 数值A / 数值B × 100%
     *
     * @param thisValue 数值A
     * @param lastValue 数值B
     * @return 百分比
     */
    public static String calPercent(Number thisValue, Number lastValue) {
        if (null == thisValue || null == lastValue || lastValue.doubleValue() == 0) {
            return "";
        }
        if (0 == thisValue.doubleValue()) {
            return "0%";
        }
        return formatPercent(NumberUtil.div(thisValue, lastValue).doubleValue(), 2);
    }

    /**
     * 同比增长百分比 = （今年指标数值 - 去年同期指标数值）/ 去年同期指标数值 × 100%
     *
     * @param thisValue 今年指标数值
     * @param lastValue 去年同期指标数值
     * @return 同比增长百分比
     */
    public static String growthPercent(Number thisValue, Number lastValue) {
        if (null == thisValue || null == lastValue) {
            return "";
        }
        return calPercent(thisValue.doubleValue() - lastValue.doubleValue(), lastValue);
    }

}
