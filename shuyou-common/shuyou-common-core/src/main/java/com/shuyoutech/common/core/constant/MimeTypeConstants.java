package com.shuyoutech.common.core.constant;

/**
 * 媒体类型常量类
 *
 * @author YangChao
 * @date 2025-07-06 12:46
 **/
public interface MimeTypeConstants {

    /**
     * png
     */
    String IMAGE_PNG = "image/png";

    /**
     * jpg
     */
    String IMAGE_JPG = "image/jpg";

    /**
     * jpeg
     */
    String IMAGE_JPEG = "image/jpeg";

    /**
     * bmp
     */
    String IMAGE_BMP = "image/bmp";

    /**
     * gif
     */
    String IMAGE_GIF = "image/gif";

    /**
     * 图片类型集合
     */
    String[] IMAGE_EXTENSION = {"bmp", "gif", "jpg", "jpeg", "png"};

    /**
     * FLASH类型集合
     */
    String[] FLASH_EXTENSION = {"swf", "flv"};

    /**
     * MEDIA类型集合
     */
    String[] MEDIA_EXTENSION = {"swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg", "asf", "rm", "rmvb"};

    /**
     * VIDEO类型集合
     */
    String[] VIDEO_EXTENSION = {"mp4", "avi", "rmvb"};

    /**
     * 默认允许文件格式
     */
    String[] DEFAULT_ALLOWED_EXTENSION = {
            // 图片
            "bmp", "gif", "jpg", "jpeg", "png",
            // word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt", "xml", "csv", "md",
            // 压缩文件
            "rar", "zip", "gz", "bz2",
            // 视频格式
            "mp4", "avi", "rmvb", "swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "mpg", "asf", "rm",
            // pdf
            "pdf"};

}
