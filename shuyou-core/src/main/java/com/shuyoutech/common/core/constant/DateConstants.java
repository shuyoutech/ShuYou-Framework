package com.shuyoutech.common.core.constant;

import java.time.ZoneId;

/**
 * 日期常量类
 *
 * @author YangChao
 * @date 2025-07-06 12:44
 **/
public interface DateConstants {

    /**
     * 日期格式化:yyyy-MM-dd HH:mm:ss
     */
    String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式：yyyy-MM-dd
     */
    String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 时间格式：HH:mm:ss
     */
    String TIME_FORMAT = "HH:mm:ss";

    /**
     * 日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss.SSS
     */
    String DATETIME_MS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 去掉-日期格式：yyyyMMddHHmmss
     */
    String PURE_DATETIME_FORMAT = "yyyyMMddHHmmss";

    /**
     * 去掉-日期格式：yyyyMMdd
     */
    String PURE_DATE_FORMAT = "yyyyMMdd";

    /**
     * 去掉-日期格式：HHmmss
     */
    String PURE_TIME_FORMAT = "HHmmss";

    /**
     * 年
     */
    String DATE_YEAR = "yyyy";

    /**
     * 月
     */
    String DATE_MONTH = "MM";

    /**
     * 天
     */
    String DATE_DAY = "dd";

    /**
     * 时
     */
    String DATE_HOUR = "HH";

    /**
     * 分
     */
    String DATE_MINUTE = "mm";

    /**
     * 秒
     */
    String DATE_SECOND = "ss";

    /**
     * 系统时间默认时区
     */
    ZoneId ZONE_ID = ZoneId.systemDefault();
}
