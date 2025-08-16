package com.shuyoutech.aigc.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum MessageTypeEnum implements BaseEnum<String, String> {

    /**
     * text
     */
    TEXT("text", "文本"),

    /**
     * image
     */
    IMAGE("image", "图片"),

    /**
     * audio
     */
    AUDIO("audio", "音频");

    private final String value;
    private final String label;

}
