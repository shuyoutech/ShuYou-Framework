package com.shuyoutech.common.core.util;

import cn.hutool.core.io.unit.DataSizeUtil;

/**
 * @author YangChao
 * @since 2025-09-18 22:29
 **/
public class DataSizeUtils extends DataSizeUtil {

    /**
     * 格式化上下文
     *
     * @param size 大小
     * @return 格式化
     */
    public static String formatContextWindow(long size) {
        if (size == 1024) {
            return "1K";
        } else if (size == 2048) {
            return "2K";
        } else if (size == 4096) {
            return "4K";
        } else if (size == 8192) {
            return "8K";
        } else if (size == 16384) {
            return "16K";
        } else if (size == 32768) {
            return "32K";
        } else if (size == 65536) {
            return "64K";
        } else if (size == 131072) {
            return "128K";
        } else if (size == 262144) {
            return "256K";
        } else if (size == 400000) {
            return "400K";
        } else if (size == 524288) {
            return "512K";
        } else if (size == 10000000) {
            return "1M";
        } else {
            return String.valueOf(size);
        }
    }

}
