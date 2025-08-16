package com.shuyoutech.aigc.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 输入输出格式类型枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum AiModalityTypeEnum implements BaseEnum<String, String> {

    TEXT("text", "文本"),

    IMAGE("image", "图片"),

    AUDIO("audio", "音频"),

    VIDEO("video", "视频");

    private final String value;
    private final String label;

}
