package com.shuyoutech.aigc.provider.service.impl;

import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import com.shuyoutech.aigc.provider.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <a href="https://stability.ai/">...</a>
 *
 * @author YangChao
 * @date 2025-07-29 16:00
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class StableDiffusionModelService  implements ModelService {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.STABLE_DIFFUSION.getValue();
    }

    // https://platform.stability.ai/docs/api-reference


}
