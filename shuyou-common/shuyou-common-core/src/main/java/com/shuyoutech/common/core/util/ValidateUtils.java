package com.shuyoutech.common.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author YangChao
 * @date 2025-07-06 14:56
 **/
public class ValidateUtils extends cn.hutool.core.lang.Validator {

    public static final String REGEX_USERNAME = "^[a-zA-Z0-9]\\w{5,20}$";

    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9~!@#]{6,20}$";

    public static Pattern PATTERN_CONTAIN_LETTER = Pattern.compile("[a-zA-Z]");

    public static Pattern PATTERN_CONTAIN_LOWER_CASE = Pattern.compile("[a-z]");

    public static Pattern PATTERN_CONTAIN_UPPER_CASE = Pattern.compile("[A-Z]");

    public static Pattern PATTERN_CONTAIN_DIGIT = Pattern.compile("[0-9]");

    public static Pattern PATTERN_CONTAIN_SPECIAL_CHAR = Pattern.compile("[\\-?~!@#$%^&*()+_=|{}\\[\\]`\"',./:;<>]+");

    /**
     * 校验用户名
     *
     * @param username 用户名
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }

    /**
     * 校验密码
     *
     * @param password 密码
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    /**
     * 判断字符串是否全中文
     */
    public static boolean isChinese(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return str.matches("[\\u4e00-\\u9fa5]+");
    }

    /**
     * 判断字符串中是否包含中文
     */
    public static boolean isContainChinese(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return ValidateUtils.hasChinese(str);
    }

    /**
     * 判断字符串是否全字母
     */
    public static boolean isLetter(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return str.matches("[a-zA-Z]+");
    }

    /**
     * 判断字符串中是否包含字母
     */
    public static boolean isContainLetter(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Matcher m = PATTERN_CONTAIN_LETTER.matcher(str);
        return m.find();
    }

    /**
     * 判断字符串中是否包含小写字母
     */
    public static boolean isContainLowerCase(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Matcher m = PATTERN_CONTAIN_LOWER_CASE.matcher(str);
        return m.find();
    }

    /**
     * 判断字符串中是否包含大写字母
     */
    public static boolean isContainUpperCase(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Matcher m = PATTERN_CONTAIN_UPPER_CASE.matcher(str);
        return m.find();
    }

    /**
     * 判断字符串中是否包含特殊字符
     */
    public static boolean isContainSpecialChar(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Matcher m = PATTERN_CONTAIN_SPECIAL_CHAR.matcher(str);
        return m.find();
    }

    /**
     * 判断字符串是否全数字
     */
    public static boolean isDigit(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return str.matches("[0-9]+");
    }

    /**
     * 判断字符串中是否包含数字
     */
    public static boolean isContainDigit(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Matcher m = PATTERN_CONTAIN_DIGIT.matcher(str);
        return m.find();
    }

}
