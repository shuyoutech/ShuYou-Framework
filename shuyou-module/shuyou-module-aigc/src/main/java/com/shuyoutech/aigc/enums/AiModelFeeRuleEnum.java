package com.shuyoutech.aigc.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模型费用规则类型枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum AiModelFeeRuleEnum implements BaseEnum<String, String> {

    INPUT_TOKEN("input_token", "输入价格"),

    OUTPUT_TOKEN("output_token", "输出价格"),

    OUTPUT_THINKING_TOKEN("output_thinking_token", "思考模式输出价格"),

    INPUT_0_128K_TOKEN("input_0_128k_token", "上下文长度0-128K 输入价格"),

    INPUT_128K_256K_TOKEN("input_128k_256k_token", "上下文长度128K-256K 输入价格"),

    INPUT_256K_1M_TOKEN("input_256k_1m_token", "上下文长度256K-1M 输入价格"),

    OUTPUT_0_128K_TOKEN("output_0_128k_token", "上下文长度0-128K 输出价格"),

    OUTPUT_128K_256K_TOKEN("output_128k_256k_token", "上下文长度128K-256K 输出价格"),

    OUTPUT_256K_1M_TOKEN("output_256k_1m_token", "上下文长度256K-1M 输出价格"),

    OUTPUT_THINKING_0_128K_TOKEN("output_thinking_0_128k_token", "上下文长度0-128K 思考模式输出价格"),

    OUTPUT_THINKING_128K_256K_TOKEN("output_thinking_128k_256k_token", "上下文长度128K-256K 思考模式输出价格"),

    OUTPUT_THINKING_256K_1M_TOKEN("output_thinking_256k_1m_token", "上下文长度256K-1M 思考模式输出价格"),

    INPUT_TEXT("input_text", "输入：文本"),

    INPUT_AUDIO("input_audio", "输入：音频"),

    INPUT_IMAGE_VIDEO("input_image_video", "输入：图片/视频"),

    OUTPUT_TEXT("output_text", "输出：文本,输入仅包含文本时"),

    OUTPUT_TEXT2("output_text2", "输出：文本,输入包含图片/音频/视频时"),

    OUTPUT_AUDIO("output_audio", "输出：文本+音频"),

    OUTPUT_NUMBER("output_number", "输出个数"),

    OUTPUT_IMAGE_LOW_1024_1024("output_image_low_1024*1024", "输出图片low-1024*1024"),

    OUTPUT_IMAGE_LOW_1024_1536("output_image_low_1024x1536", "输出图片low-1024x1536"),

    OUTPUT_IMAGE_LOW_1536_1024("output_image_low_1536x1024", "输出图片low-1536x1024"),

    OUTPUT_IMAGE_MEDIUM_1024_1024("output_image_medium_1024*1024", "输出图片medium-1024*1024"),

    OUTPUT_IMAGE_MEDIUM_1024_1536("output_image_medium_1024x1536", "输出图片medium-1024x1536"),

    OUTPUT_IMAGE_MEDIUM_1536_1024("output_image_medium_1536x1024", "输出图片medium-1536x1024"),

    OUTPUT_IMAGE_HIGH_1024_1024("output_image_high_1024*1024", "输出图片High-1024*1024"),

    OUTPUT_IMAGE_HIGH_1024_1536("output_image_high_1024x1536", "输出图片High-1024x1536"),

    OUTPUT_IMAGE_HIGH_1536_1024("output_image_high_1536x1024", "输出图片High-1536x1024"),

    OUTPUT_IMAGE_STANDARD_1024_1024("output_image_standard_1024*1024", "输出图片Standard-1024*1024"),

    OUTPUT_IMAGE_STANDARD_1024_1536("output_image_standard_1024x1536", "输出图片Standard-1024x1536"),

    OUTPUT_IMAGE_STANDARD_1536_1024("output_image_standard_1536x1024", "输出图片Standard-1536x1024"),

    OUTPUT_IMAGE_HD_1024_1024("output_image_hd_1024*1024", "输出图片hd-1024*1024"),

    OUTPUT_IMAGE_HD_1024_1536("output_image_hd_1024x1536", "输出图片hd-1024x1536"),

    OUTPUT_IMAGE_HD_1536_1024("output_image_hd_1536x1024", "输出图片hd-1536x1024"),

    OUTPUT_IMAGE_720P("output_image_720p", "输出图片720p"),

    OUTPUT_IMAGE_1080P("output_image_1080p", "输出图片1080p"),

    OUTPUT_DURATION("output_duration", "输出时长"),

    OUTPUT_DURATION_480P("output_duration_480p", "输出时长480p"),

    OUTPUT_DURATION_1080P("output_duration_1080p", "输出时长1080p");

    private final String value;
    private final String label;

}
