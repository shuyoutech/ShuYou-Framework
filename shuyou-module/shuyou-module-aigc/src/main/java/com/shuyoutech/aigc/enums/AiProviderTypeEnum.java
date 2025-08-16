package com.shuyoutech.aigc.enums;

import com.shuyoutech.common.core.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 供应商类型枚举
 *
 * @author YangChao
 * @date 2025-05-12 12:00
 **/
@Getter
@AllArgsConstructor
public enum AiProviderTypeEnum implements BaseEnum<String, String> {

    /**
     * 通义千问
     */
    ALIYUN("aliyun", "阿里通义"),

    /**
     * DeepSeek
     */
    DEEPSEEK("deepseek", "DeepSeek"),

    /**
     * ChatGPT
     */
    OPENAI("openai", "OpenAI"),

    /**
     * Gemini
     */
    GOOGLE("google", "Google"),

    /**
     * Claude
     */
    ANTHROPIC("anthropic", "Anthropic"),

    /**
     * grok
     */
    XAI("xai", "xAI"),

    /**
     * 千帆 文心一言
     */
    BAIDU("baidu", "百度千帆"),

    /**
     * 豆包 火山方舟
     */
    DOUYIN("douyin", "抖音豆包"),

    /**
     * 讯飞星火
     */
    XFYUN("xfyun", "讯飞星火"),

    /**
     * 智谱清言
     */
    ZHIPU("zhipu", "智谱清言"),

    /**
     * 硅基流动
     */
    SILICONFLOW("siliconflow", "硅基流动"),

    /**
     * lingyiwanwu
     */
    LINGYIWANWU("lingyiwanwu", "零一万物"),

    /**
     * 模力方舟
     */
    GITEEAI("gitee", "模力方舟"),

    /**
     * 腾讯混元
     */
    TENCENT("tencent", "腾讯混元"),

    /**
     * 阶跃星辰
     */
    STEPFUN("stepfun", "阶跃星辰"),

    /**
     * MiniMax
     */
    MINIMAX("minimax", "MiniMax"),

    /**
     * Ollama
     */
    OLLAMA("ollama", "Ollama"),

    /**
     * Stable Diffusion
     */
    AMAZON("amazon", "Amazon"),

    /**
     * gen4_image
     */
    RUNWAY("runway", "Runway"),

    /**
     * stability
     */
    STABLE_DIFFUSION("stability", "stability"),


    /**
     * 快手 可灵AI
     */
    KLING("kling", "可灵AI"),

    /**
     * OpenRouter代理
     */
    OPEN_ROUTER("openrouter", "OpenRouter代理"),


    ;


    private final String value;
    private final String label;

}
