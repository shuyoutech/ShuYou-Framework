package com.shuyoutech.aigc.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * token计费类型枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum AiChargeTypeEnum implements BaseEnum<String, String> {

    QUANTITY("quantity", "按量计费"),

    FREQUENCY("frequency", "按次计费"),

    IMAGE_QUALITY("image_quality", "按图片质量计费"),

    FREE("free", "免费");

    private final String value;
    private final String label;

}
