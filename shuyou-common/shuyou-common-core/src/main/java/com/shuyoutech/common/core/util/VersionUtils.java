package com.shuyoutech.common.core.util;

/**
 * @author YangChao
 * @date 2025-07-04 14:47
 **/
public class VersionUtils {

    public static final String INIT_VERSION = "1.0";

    /**
     * 获取下一个版本号
     *
     * @param currentVersion 当前版本号
     * @param minorVersion   最小版本
     * @return 版本号
     */
    public static String getNextVersion(String currentVersion, boolean minorVersion) {
        String[] versionSplit = currentVersion.split("\\.");
        return minorVersion ? versionSplit[0] + "." + (Integer.parseInt(versionSplit[1]) + 1) : Integer.parseInt(versionSplit[0]) + 1 + "." + versionSplit[1];
    }

    /**
     * 获取下一个版本号
     *
     * @param currentVersion 当前版本号
     * @return 版本号
     */
    public static String getNextVersion(String currentVersion) {
        return getNextVersion(currentVersion, false);
    }

}
