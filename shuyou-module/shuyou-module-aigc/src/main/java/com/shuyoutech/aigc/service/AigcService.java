package com.shuyoutech.aigc.service;

import com.shuyoutech.aigc.domain.bo.ChatModelBo;
import com.shuyoutech.aigc.domain.bo.CommonModelBo;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author YangChao
 * @date 2025-07-13 15:50
 **/
public interface AigcService {

    void chat(ChatModelBo bo, HttpServletResponse response);

    void model(CommonModelBo bo, HttpServletResponse response);

}
