package com.shuyoutech.common.core.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.shuyoutech.common.core.constant.NumberConstants;

import java.time.*;
import java.util.Date;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-06 13:41
 **/
public class DateUtils extends DateUtil {

    /**
     * LocalDateTime 转换为 Date
     *
     * @param localDateTime LocalDateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * LocalDate 转换为 Date
     *
     * @param localDate LocalDate
     * @return Date
     */
    public static Date toDate(LocalDate localDate) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.of(0, 0, 0));
        return toDate(localDateTime);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 毫秒转换位字符串
     *
     * @param lastMillisecond 毫秒
     * @return 字符串
     */
    public static String millisecondToTime(Long lastMillisecond) {
        long milliseconds = System.currentTimeMillis() - lastMillisecond;
        if (milliseconds < 0) {
            throw new IllegalArgumentException("Seconds must be a positive number!");
        }
        if (milliseconds < NumberConstants.ONE_THOUSAND) {
            return milliseconds + "毫秒";
        }
        long seconds = milliseconds / 1000;
        long day = seconds / 86400;
        long hour = (seconds / 3600) % 24;
        long minute = (seconds / 60) % 60;
        long second = seconds % 60;
        final StringBuilder sb = new StringBuilder();
        if (day > NumberConstants.ZERO) {
            sb.append(day);
            sb.append("天");
            sb.append(hour);
            sb.append("小时");
            sb.append(minute);
            sb.append("分");
            sb.append(second);
            sb.append("秒");
        } else if (hour > NumberConstants.ZERO) {
            sb.append(hour);
            sb.append("小时");
            sb.append(minute);
            sb.append("分");
            sb.append(second);
            sb.append("秒");
        } else if (minute > NumberConstants.ZERO) {
            sb.append(minute);
            sb.append("分");
            sb.append(second);
            sb.append("秒");
        } else {
            sb.append(second);
            sb.append("秒");
        }
        return sb.toString();
    }

    /**
     * 近minutes集合
     *
     * @param minutes 分钟
     * @return List
     */
    public static List<String> dateRangeMinute(int minutes) {
        Date end = new Date();
        Date start = DateUtil.offsetMinute(end, -minutes + 1);
        List<DateTime> dateTimes = DateUtil.rangeToList(start, end, DateField.MINUTE);
        List<String> result = CollectionUtils.newArrayList();
        for (DateTime time : dateTimes) {
            result.add(time.toString("yyyy-MM-dd HH:mm"));
        }
        return result;
    }

    /**
     * 近hours集合
     *
     * @param hours 小时
     * @return List
     */
    public static List<String> dateRangeHour(int hours) {
        Date end = new Date();
        Date start = DateUtil.offsetHour(end, -hours + 1);
        List<DateTime> dateTimes = DateUtil.rangeToList(start, end, DateField.HOUR);
        List<String> result = CollectionUtils.newArrayList();
        for (DateTime time : dateTimes) {
            result.add(time.toString("yyyy-MM-dd HH"));
        }
        return result;
    }

    /**
     * 近days日期集合
     *
     * @param days 天数
     * @return List
     */
    public static List<String> dateRangeDay(int days) {
        Date end = new Date();
        Date start = DateUtil.offsetDay(end, -days + 1);
        List<DateTime> dateTimes = DateUtil.rangeToList(start, end, DateField.DAY_OF_YEAR);
        List<String> result = CollectionUtils.newArrayList();
        for (DateTime time : dateTimes) {
            result.add(time.toDateStr());
        }
        return result;
    }

    /**
     * 近months月集合
     *
     * @param months 月份
     * @return List
     */
    public static List<String> dateRangMonth(int months) {
        Date end = new Date();
        Date start = DateUtil.offsetMonth(end, -months + 1);
        List<DateTime> dateTimes = DateUtil.rangeToList(start, end, DateField.MONTH);
        List<String> result = CollectionUtils.newArrayList();
        for (DateTime time : dateTimes) {
            result.add(time.toString("yyyy-MM"));
        }
        return result;
    }

    /**
     * 指定某个月有多少天集合
     *
     * @param month 月份
     * @return 天数集合
     */
    public static List<String> dayByMonth(String month) {
        DateTime monthDate = DateUtil.parse(month, "yyyy-MM");
        DateTime beginMonth = DateUtil.beginOfMonth(monthDate);
        DateTime endMonth = DateUtil.endOfMonth(monthDate);
        List<DateTime> dateTimes = DateUtil.rangeToList(beginMonth, endMonth, DateField.DAY_OF_MONTH);
        List<String> result = CollectionUtils.newArrayList();
        for (DateTime time : dateTimes) {
            result.add(time.toString("yyyy-MM-dd"));
        }
        return result;
    }

    /**
     * 判断字符串是否为指定格式的日期时间
     *
     * @param dateStr    需要检查的日期字符串
     * @param dateFormat 指定的日期格式，例如："yyyyMMdd", "yyyy-MM-dd", "yyyy/MM/dd" 等
     * @return 如果字符串是指定格式的日期时间，返回 true;否则返回 false。
     */
    public static boolean isValidDateFormat(String dateStr, String dateFormat) {
        if (StringUtils.isEmpty(dateStr) || StringUtils.isEmpty(dateFormat)) {
            return false;
        }
        try {
            // 将字符串解析为日期对象，如果解析成功，则说明字符串是有效的日期格式；否则说明字符串不是有效的日期格式。
            DateUtil.parse(dateStr, dateFormat);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
