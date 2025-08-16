package com.shuyoutech.aigc.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模型功能枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum AiModelFunctionEnum implements BaseEnum<String, String> {

    CHAT("chat", "对话"),

    LONG("long", "长上下文"),

    MATH("math", "数学"),

    CODER("coder", "代码"),

    MT("mt", "翻译"),

    CHARACTER("character", "角色扮演"),

    DOC("doc", "数据挖掘"),

    TEXT_TO_IMAGE("textToImage", "文生图"),

    IMAGE_TO_IMAGE("imageToImage", "图生图"),

    IMAGE_EDIT("imageEdit", "图像编辑"),

    IMAGE_VARIATION("imageVariation", "图像变形"),

    IMAGE_VIRTUAL_TRY_ON("imageVirtualTryOn", "虚拟试穿"),

    IMAGE_BACKGROUND("imageBackground", "图像背景"),

    WORDART_SEMANTIC("wordartSemantic", "文字变形"),

    WORDART_TEXTURE("wordartTexture", "文字纹理"),

    TEXT_TO_VIDEO("textToVideo", "文生视频"),

    IMAGE_TO_VIDEO("imageToVideo", "图生视频"),

    MULTI_IMAGE_TO_VIDEO("multiImageToVideo", "多图参考生视频"),

    VIDEO_EXTEND("videoExtend", "视频-延长"),

    VIDEO_LIP_SYNC("videoLipSync", "视频-对口型"),

    VIDEO_EFFECTS("videoEffects", "视频-特效"),

    UPSCALE_VIDEO("upscaleVideo", "视频-升级高清"),

    VIDEO_CHARACTER_PERFORMANCE("videoCharacterPerformance", "视频-角色表演"),

    AUDIO_SPEECH("audioSpeech", "文本转音频/语音合成"),

    AUDIO_TRANSCRIPTION("audioTranscription", "音频转文本/语音识别"),

    AUDIO_TRANSLATION("audioTranslation", "音频/语音翻译"),

    EMBEDDING("embedding", "向量"),

    ;

    private final String value;
    private final String label;

}
