package com.shuyoutech.aigc.provider.service.impl;

import com.shuyoutech.aigc.enums.AiProviderTypeEnum;
import com.shuyoutech.aigc.provider.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author YangChao
 * @date 2025-07-29 15:08
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class LumaModelService  implements ModelService {

    @Override
    public String providerName() {
        return AiProviderTypeEnum.RUNWAY.getValue();
    }


}
