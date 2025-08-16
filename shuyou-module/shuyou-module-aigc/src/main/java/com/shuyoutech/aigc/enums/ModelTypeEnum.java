package com.shuyoutech.aigc.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模型类型枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum ModelTypeEnum implements BaseEnum<String, String> {

    TEXT_GENERATION("TG", "文本生成"),

    OMNI("OMNI", "全模态"),

    QWEN_WITH_QUESTIONS("QwQ", "推理模型"),

    AUDIO_UNDERSTANDING("AU", "音频理解"),

    VIDEO_UNDERSTANDING("VU", "视频理解"),

    IMAGE_UNDERSTANDING("IU", "图片理解"),

    VIDEO_GENERATION("VG", "视频生成"),

    IMAGE_GENERATION("IG", "图片生成"),

    IMAGE_PROCESSING("IP", "图片处理"),

    TEXT_EMBEDDING("TE", "文本向量化"),

    IMAGE_TEXT_EMBEDDING("ITE", "图文多模态向量化"),

    TEXT_TO_SPEECH("TTS", "语音合成"),

    AUTOMATIC_SPEECH_RECOGNITION("ASR", "语音识别"),

    RANKING_MODEL("RK", "排序模型"),

    OMNI_MODERATION("OM", "多模态审核模型");

    private final String value;
    private final String label;

}
