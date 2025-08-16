package com.shuyoutech.aigc.provider.service;

import com.shuyoutech.aigc.domain.model.ChatModelBuilder;
import com.shuyoutech.aigc.domain.model.CommonModelBuilder;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 模型公共的API功能
 *
 * @author YangChao
 * @date 2025-07-13 20:07
 **/
public interface ModelService {

    /**
     * 模型供应商名称
     */
    String providerName();

    /**
     * 对话模型
     */
    default void chat(ChatModelBuilder builder, HttpServletResponse response) {
    }

    /**
     * 图片模型
     */
    default void image(CommonModelBuilder builder, HttpServletResponse response) {
    }

    /**
     * 视频模型
     */
    default void video(CommonModelBuilder builder, HttpServletResponse response) {
    }

    /**
     * 音频模型
     */
    default void audio(CommonModelBuilder builder, HttpServletResponse response) {
    }

}
