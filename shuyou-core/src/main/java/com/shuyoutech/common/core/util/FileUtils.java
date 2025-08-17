package com.shuyoutech.common.core.util;

import cn.hutool.core.io.FileUtil;
import com.shuyoutech.common.core.constant.CommonConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author YangChao
 * @date 2025-07-06 13:49
 **/
@Slf4j
public class FileUtils extends FileUtil {

    /**
     * 下载文件名重新编码
     *
     * @param request  请求对象
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    public static String encodeFileName(HttpServletRequest request, String fileName) {
        try {
            if (StringUtils.isBlank(fileName)) {
                return null;
            }
            String agent = request.getHeader(CommonConstants.USER_AGENT);
            String encodeName;
            if (StringUtils.containsIgnoreCase(agent, CommonConstants.FIRE_FOX)) {
                encodeName = new String(fileName.getBytes(), StandardCharsets.ISO_8859_1);
            } else {
                encodeName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            }
            return encodeName.replaceAll("\\+", "%20");
        } catch (Exception e) {
            log.error("encodeFileName ===================== exception:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 下载文件名重新编码
     *
     * @param response 响应对象
     * @param fileName 真实文件名
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String fileName) {
        try {
            response.addHeader(CommonConstants.HEADER_ACCESS_CONTROL_EXPOSE, "Content-Disposition,download-filename");
            response.setHeader(CommonConstants.HEADER_CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        } catch (Exception e) {
            log.error("setAttachmentResponseHeader ===================== exception:{}", e.getMessage());
        }
    }

    /**
     * 从文件中读取每一行数据，编码为UTF-8，且以指定符号分隔
     *
     * @param filePath  文件路径
     * @param separator 分隔符
     * @return List
     */
    public static List<List<String>> readUtf8Lines(String filePath, CharSequence separator) {
        List<List<String>> result = CollectionUtils.newArrayList();
        List<String> lines = FileUtil.readUtf8Lines(filePath);
        if (CollectionUtils.isEmpty(lines)) {
            return result;
        }
        List<String> list;
        for (String line : lines) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            list = StringUtils.split(line, separator);
            result.add(list);
        }
        return result;
    }

    /**
     * 从文件中读取每一行数据，编码为UTF-8，且以指定符号分隔且转换为Map
     *
     * @param path       文件路径
     * @param separator  分隔符
     * @param keyIndex   key索引
     * @param valueIndex value索引
     * @return Map
     */
    public static Map<String, String> readUtf8LinesToMap(String path, CharSequence separator, int keyIndex, int valueIndex) {
        Map<String, String> result = MapUtils.newHashMap();
        List<String> lines = FileUtil.readUtf8Lines(path);
        if (CollectionUtils.isEmpty(lines)) {
            return result;
        }
        List<String> list;
        for (String line : lines) {
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            list = StringUtils.split(line, separator);
            if (list.size() - 1 < keyIndex || list.size() - 1 < valueIndex) {
                continue;
            }
            if (valueIndex < 0) {
                result.put(list.get(keyIndex), line);
            } else {
                result.put(list.get(keyIndex), list.get(valueIndex));
            }
        }
        return result;
    }

}
