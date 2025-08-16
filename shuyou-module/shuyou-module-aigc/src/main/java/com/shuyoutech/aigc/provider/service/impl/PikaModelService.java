package com.shuyoutech.aigc.provider.service.impl;

import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import com.shuyoutech.aigc.provider.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <a href="https://pika.art/">适合动画风格创作需求用户（如上半身特写、短时长动画）、对生成速度敏感但接受短时长的用户。</a>
 *
 * @author YangChao
 * @date 2025-07-29 14:12
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class PikaModelService  implements ModelService {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.RUNWAY.getValue();
    }





}
