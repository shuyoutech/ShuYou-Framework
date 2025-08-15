package com.shuyoutech.common.core.util;

import java.util.Arrays;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-07-06 14:53
 **/
public class PasswordCheckUtils {

    /**
     * 密码最小长度，默认为8
     */
    public static String MIN_LENGTH = "8";
    /**
     * 密码最大长度，默认为20
     */
    public static String MAX_LENGTH = "20";
    /**
     * 不允许最小的连续个数
     */
    public static Integer LIMIT_NUM_KEY = 4;
    /**
     * 键盘横向方向规则
     */
    public static String[] KEYBOARD_HORIZONTAL_ARR = {"01234567890", "qwertyuiop", "asdfghjkl", "zxcvbnm",};
    /**
     * 键盘斜线方向规则
     */
    public static String[] KEYBOARD_SLOPE_ARR = {"1qaz", "2wsx", "3edc", "4rfv", "5tgb", "6yhn", "7ujm", "8ik,", "9ol.", "0p;/", "=[;.", "-pl,", "0okm", "9ijn", "8uhb", "7ygv", "6tfc", "5rdx", "4esz"};
    /**
     * 常用词库
     */
    public static String[] SIMPLE_WORDS = {"admin", "szim", "epicrouter", "password", "grouter", "dare", "root", "guest", "user", "success", "pussy", "mustang", "fuckme", "jordan", "test", "hunter", "jennifer", "batman", "thomas", "soccer", "sexy", "killer", "george", "asshole", "fuckyou", "summer", "hello", "secret", "fucker", "enter", "cookie", "administrator", "xiaoming", "taobao", "iloveyou", "woaini", "982464", "monkey", "letmein", "trustno1", "dragon", "baseball", "master", "sunshine", "ashley", "bailey", "shadow", "superman", "football", "michael", "qazwsx"};

    /**
     * 评估密码中包含的字符类型是否符合要求
     *
     * @param password 密码字符串
     * @return 符合要求 返回true
     */
    public static boolean evalPassword(String password) {
        if (StringUtils.isEmpty(password)) {
            return false;
        }

        // 检测长度
        boolean flag = checkPasswordLength(password);
        if (!flag) {
            return false;
        }

        // 检测包含数字
        flag = checkContainDigit(password);
        if (!flag) {
            return false;
        }

        // 检测包含字母
        flag = checkContainCase(password);
        if (!flag) {
            return false;
        }

        // 检测包含小写字母
        flag = checkContainLowerCase(password);
        if (!flag) {
            return false;
        }

        // 检测包含大写字母
        flag = checkContainUpperCase(password);
        if (!flag) {
            return false;
        }

        // 检测包含特殊符号
        flag = checkContainSpecialChar(password);
        if (!flag) {
            return false;
        }

        // 检测键盘横向连续
        flag = checkLateralKeyboardSite(password);
        if (flag) {
            return false;
        }

        // 检测键盘斜向连续
        flag = checkKeyboardSlantSite(password);
        if (flag) {
            return false;
        }

        // 检测逻辑位置连续
        flag = checkSequentialChars(password);
        if (flag) {
            return false;
        }

        // 检测相邻字符是否相同
        flag = checkSequentialSameChars(password);
        if (flag) {
            return false;
        }

        // 检测常用词库
        flag = checkSimpleWord(password);
        return !flag;
    }

    /**
     * 检测密码中字符长度
     *
     * @param password 密码字符串
     * @return 符合长度要求 返回true
     */
    public static boolean checkPasswordLength(String password) {
        return password.length() >= Integer.parseInt(MIN_LENGTH) && password.length() <= Integer.parseInt(MAX_LENGTH);
    }

    /**
     * 检测密码中是否包含数字
     *
     * @param password 密码字符串
     * @return 包含数字 返回true
     */
    public static boolean checkContainDigit(String password) {
        return ValidateUtils.isContainDigit(password);
    }

    /**
     * 检测密码中是否包含字母（不区分大小写）
     *
     * @param password 密码字符串
     * @return 包含字母 返回true
     */
    public static boolean checkContainCase(String password) {
        return ValidateUtils.isContainLetter(password);
    }

    /**
     * 检测密码中是否包含小写字母
     *
     * @param password 密码字符串
     * @return 包含小写字母 返回true
     */
    public static boolean checkContainLowerCase(String password) {
        return ValidateUtils.isContainLowerCase(password);
    }

    /**
     * 检测密码中是否包含大写字母
     *
     * @param password 密码字符串
     * @return 包含大写字母 返回true
     */
    public static boolean checkContainUpperCase(String password) {
        return ValidateUtils.isContainUpperCase(password);
    }

    /**
     * 检测密码中是否包含特殊符号
     *
     * @param password 密码字符串
     * @return 包含特殊符号 返回true
     */
    public static boolean checkContainSpecialChar(String password) {
        return ValidateUtils.isContainSpecialChar(password);
    }

    /**
     * 键盘规则匹配器 横向连续检测
     *
     * @param password 密码字符串
     * @return 含有横向连续字符串 返回true
     */
    public static boolean checkLateralKeyboardSite(String password) {
        int n = password.length();
        String temp;
        String revOrderStr;
        for (int i = 0; i + LIMIT_NUM_KEY <= n; i++) {
            temp = password.toLowerCase().substring(i, i + LIMIT_NUM_KEY);
            for (String configStr : KEYBOARD_HORIZONTAL_ARR) {
                revOrderStr = new StringBuffer(configStr).reverse().toString();
                if (configStr.contains(temp)) {
                    return true;
                }
                //考虑逆序输入情况下 连续输入
                if (revOrderStr.contains(temp)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 键盘规则匹配器 斜向规则检测
     *
     * @param password 密码字符串
     * @return 含有斜向连续字符串 返回true
     */
    public static boolean checkKeyboardSlantSite(String password) {
        int n = password.length();
        String temp;
        String revOrderStr;
        for (int i = 0; i + LIMIT_NUM_KEY <= n; i++) {
            temp = password.toLowerCase().substring(i, i + LIMIT_NUM_KEY);
            for (String configStr : KEYBOARD_SLOPE_ARR) {
                revOrderStr = new StringBuffer(configStr).reverse().toString();
                if (configStr.contains(temp)) {
                    return true;
                }
                //考虑逆序输入情况下 连续输入
                if (revOrderStr.contains(temp)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 评估a-z,z-a这样的连续字符
     *
     * @param password 密码字符串
     * @return 含有a-z,z-a连续字符串 返回true
     */
    public static boolean checkSequentialChars(String password) {
        password = password.toLowerCase();
        int n = password.length();
        char[] pwdCharArr = password.toCharArray();
        int normalCount;
        int reversedCount;
        for (int i = 0; i + LIMIT_NUM_KEY <= n; i++) {
            normalCount = 0;
            reversedCount = 0;
            for (int j = 0; j < LIMIT_NUM_KEY - 1; j++) {
                if (pwdCharArr[i + j + 1] - pwdCharArr[i + j] == 1) {
                    normalCount++;
                    if (normalCount == LIMIT_NUM_KEY - 1) {
                        return true;
                    }
                }
                if (pwdCharArr[i + j] - pwdCharArr[i + j + 1] == 1) {
                    reversedCount++;
                    if (reversedCount == LIMIT_NUM_KEY - 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 评估aaaa, 1111这样的相同连续字符
     *
     * @param password 密码字符串
     * @return 含有aaaa, 1111等连续字符串 返回true
     */
    public static boolean checkSequentialSameChars(String password) {
        int n = password.length();
        char[] pwdCharArr = password.toCharArray();
        boolean flag = false;
        int count;
        for (int i = 0; i + LIMIT_NUM_KEY <= n; i++) {
            count = 0;
            for (int j = 0; j < LIMIT_NUM_KEY - 1; j++) {
                if (pwdCharArr[i + j] == pwdCharArr[i + j + 1]) {
                    count++;
                    if (count == LIMIT_NUM_KEY - 1) {
                        return true;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 检测常用词库
     *
     * @param password 密码
     * @return boolean
     */
    public static boolean checkSimpleWord(String password) {
        List<String> simpleWords = Arrays.asList(SIMPLE_WORDS);
        return simpleWords.contains(password.toLowerCase());
    }

}
