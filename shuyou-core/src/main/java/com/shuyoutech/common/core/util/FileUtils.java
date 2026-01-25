package com.shuyoutech.common.core.util;

import cn.hutool.core.io.FileUtil;
import com.shuyoutech.common.core.constant.CommonConstants;
import com.shuyoutech.common.core.constant.StringConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YangChao
 * @since 2025-07-06 13:49
 **/
@Slf4j
public class FileUtils extends FileUtil {

    /**
     * MIME 类型到文件扩展名的映射表
     */
    private static final Map<String, String> MIME_TYPE_TO_EXTENSION_MAP = new HashMap<>();

    static {
        // 图片类型
        MIME_TYPE_TO_EXTENSION_MAP.put("image/png", "png");
        MIME_TYPE_TO_EXTENSION_MAP.put("image/jpeg", "jpg");
        MIME_TYPE_TO_EXTENSION_MAP.put("image/jpg", "jpg");
        MIME_TYPE_TO_EXTENSION_MAP.put("image/gif", "gif");
        MIME_TYPE_TO_EXTENSION_MAP.put("image/bmp", "bmp");
        MIME_TYPE_TO_EXTENSION_MAP.put("image/webp", "webp");
        MIME_TYPE_TO_EXTENSION_MAP.put("image/svg+xml", "svg");
        MIME_TYPE_TO_EXTENSION_MAP.put("image/x-icon", "ico");
        MIME_TYPE_TO_EXTENSION_MAP.put("image/tiff", "tiff");

        // 文档类型
        MIME_TYPE_TO_EXTENSION_MAP.put("application/pdf", "pdf");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/msword", "doc");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/vnd.ms-excel", "xls");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/vnd.ms-powerpoint", "ppt");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/rtf", "rtf");

        // 文本类型
        MIME_TYPE_TO_EXTENSION_MAP.put("text/plain", "txt");
        MIME_TYPE_TO_EXTENSION_MAP.put("text/html", "html");
        MIME_TYPE_TO_EXTENSION_MAP.put("text/xml", "xml");
        MIME_TYPE_TO_EXTENSION_MAP.put("text/css", "css");
        MIME_TYPE_TO_EXTENSION_MAP.put("text/javascript", "js");
        MIME_TYPE_TO_EXTENSION_MAP.put("text/csv", "csv");
        MIME_TYPE_TO_EXTENSION_MAP.put("text/markdown", "md");

        // 压缩文件类型
        MIME_TYPE_TO_EXTENSION_MAP.put("application/zip", "zip");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/x-rar-compressed", "rar");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/x-tar", "tar");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/gzip", "gz");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/x-bzip2", "bz2");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/x-7z-compressed", "7z");

        // 音频类型
        MIME_TYPE_TO_EXTENSION_MAP.put("audio/mpeg", "mp3");
        MIME_TYPE_TO_EXTENSION_MAP.put("audio/wav", "wav");
        MIME_TYPE_TO_EXTENSION_MAP.put("audio/x-ms-wma", "wma");
        MIME_TYPE_TO_EXTENSION_MAP.put("audio/midi", "mid");
        MIME_TYPE_TO_EXTENSION_MAP.put("audio/ogg", "ogg");
        MIME_TYPE_TO_EXTENSION_MAP.put("audio/aac", "aac");

        // 视频类型
        MIME_TYPE_TO_EXTENSION_MAP.put("video/mp4", "mp4");
        MIME_TYPE_TO_EXTENSION_MAP.put("video/avi", "avi");
        MIME_TYPE_TO_EXTENSION_MAP.put("video/x-msvideo", "avi");
        MIME_TYPE_TO_EXTENSION_MAP.put("video/x-ms-wmv", "wmv");
        MIME_TYPE_TO_EXTENSION_MAP.put("video/x-flv", "flv");
        MIME_TYPE_TO_EXTENSION_MAP.put("video/x-shockwave-flash", "swf");
        MIME_TYPE_TO_EXTENSION_MAP.put("video/quicktime", "mov");
        MIME_TYPE_TO_EXTENSION_MAP.put("video/x-ms-asf", "asf");
        MIME_TYPE_TO_EXTENSION_MAP.put("video/x-rmvb", "rmvb");
        MIME_TYPE_TO_EXTENSION_MAP.put("video/mpeg", "mpg");
        MIME_TYPE_TO_EXTENSION_MAP.put("video/x-rm", "rm");

        // 其他类型
        MIME_TYPE_TO_EXTENSION_MAP.put("application/json", "json");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/xml", "xml");
        MIME_TYPE_TO_EXTENSION_MAP.put("application/octet-stream", "bin");
    }

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
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
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
            log.error(e.getMessage(), e);
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
     * @param keyIndex   索引
     * @param valueIndex 索引
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

    /**
     * 根据 MIME 类型获取文件扩展名
     *
     * @param mimeType MIME类型，例如：image/png, application/pdf
     * @return 文件扩展名（不含点号），如果未找到则返回null
     */
    public static String getExtensionFromMimeType(String mimeType) {
        if (StringUtils.isBlank(mimeType)) {
            return null;
        }
        // 去除可能的参数，例如：image/png;charset=utf-8 -> image/png
        List<String> arrList = StringUtils.split(mimeType, StringConstants.SEMICOLON);
        return MIME_TYPE_TO_EXTENSION_MAP.get(arrList.getFirst().trim().toLowerCase());
    }

}
