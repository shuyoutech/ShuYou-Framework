package com.shuyoutech.common.core.util;

import cn.hutool.core.io.unit.DataSize;
import cn.hutool.core.io.unit.DataSizeUtil;
import com.shuyoutech.common.core.constant.StringConstants;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 格式化工具类
 *
 * @author YangChao
 * @date 2025-07-06 14:24
 **/
public class FormatUtils {

    /**
     * 解析数据大小字符串，转换为bytes大小
     *
     * @param text 数据大小字符串，类似于：12KB, 5MB等
     * @return bytes大小
     */
    public static long parse(String text) {
        return DataSize.parse(text).toBytes();
    }

    /**
     * 格式化字节 转 "B", "KB", "MB", "GB", "TB", "EB"
     *
     * @param size 字节大小
     * @return 大小
     */
    public static String formatDataByte(long size) {
        return DataSizeUtil.format(size).replace(StringConstants.SPACE, StringConstants.EMPTY);
    }

    /**
     * 格式化字节 转 KB
     *
     * @param size 字节大小
     * @return 大小
     */
    public static String formatDataByteToKb(long size) {
        if (size <= 0) {
            return "0";
        }
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, 1));
    }

    /**
     * 格式化字节 转 MB
     *
     * @param size 字节大小
     * @return 大小
     */
    public static String formatDataByteToMb(long size) {
        if (size <= 0) {
            return "0";
        }
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, 2));
    }

    /**
     * 格式化字节 转 GB
     *
     * @param size 字节大小
     * @return 大小
     */
    public static String formatDataByteToGb(long size) {
        if (size <= 0) {
            return "0";
        }
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, 3));
    }

    /**
     * 格式化字节 转 TB
     *
     * @param size 字节大小
     * @return 大小
     */
    public static String formatDataByteToTb(long size) {
        if (size <= 0) {
            return "0";
        }
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, 4));
    }

    /**
     * 格式化利率
     *
     * @param rate 利率
     * @return 格式化利率%
     */
    public static String formatRate(Double rate) {
        return formatRate(rate, 4);
    }

    /**
     * 格式化利率
     *
     * @param rate 利率
     * @return 格式化利率%
     */
    public static String formatRate(Double rate, int scale) {
        if (null == rate) {
            return "0%";
        }
        BigDecimal mul = NumberUtils.mul(BigDecimal.valueOf(rate), BigDecimal.valueOf(100));
        String result = NumberUtils.roundStr(mul.doubleValue(), scale);
        return formatDotZero(result) + "%";
    }

    /**
     * 格式化金额
     *
     * @param amt 金额
     * @return 格式化金额
     */
    public static String formatAmt(BigDecimal amt) {
        if (null == amt) {
            return "0";
        }
        double yiAmt = 100000000;
        if (amt.doubleValue() >= yiAmt) {
            return formatAmtYiYuan(amt);
        }
        return formatAmtWanYuan(amt);
    }

    /**
     * 格式化金额-万元
     *
     * @param amt 金额
     * @return 格式化金额-万元
     */
    public static String formatAmtWanYuan(BigDecimal amt) {
        if (null == amt) {
            return "0";
        }
        BigDecimal mul = NumberUtils.div(amt, BigDecimal.valueOf(10000));
        String result = NumberUtils.roundStr(mul.doubleValue(), 2);
        return formatDotZero(result) + "万";
    }

    /**
     * 格式化金额-亿元
     *
     * @param amt 金额
     * @return 格式化金额-亿元
     */
    public static String formatAmtYiYuan(BigDecimal amt) {
        if (null == amt) {
            return "0";
        }
        BigDecimal mul = NumberUtils.div(amt, BigDecimal.valueOf(100000000));
        String result = NumberUtils.roundStr(mul.doubleValue(), 2);
        return formatDotZero(result) + "亿";
    }

    /**
     * 格式化小数点后面的0
     *
     * @param value 值
     * @return 格式化小数点后面的0
     */
    public static String formatDotZero(String value) {
        if (!value.contains(StringConstants.DOT)) {
            return value;
        }
        String[] arr = StringUtils.splitToArray(value, StringConstants.DOT);
        String str1 = arr[0];
        String str2 = arr[1];
        int length = str2.length();
        int end = length;
        for (int i = length - 1; i >= 0; i--) {
            if (str2.charAt(i) == '0') {
                end = i;
                continue;
            }
            break;
        }
        str2 = str2.substring(0, end);
        if (StringUtils.isEmpty(str2)) {
            return str1;
        }
        return str1 + "." + str2;
    }

}
