package com.shuyoutech.aigc.listener;

import com.shuyoutech.aigc.provider.AigcAppFactory;
import com.shuyoutech.aigc.provider.AigcKnowledgeFactory;
import com.shuyoutech.aigc.provider.AigcModelFactory;
import com.shuyoutech.aigc.provider.AigcVectorStoreFactory;
import com.shuyoutech.aigc.provider.build.ModelBuildHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.shuyoutech.aigc.provider.AigcModelFactory.MODEL_STRATEGY_MAP;

/**
 * @author YangChao
 * @date 2025-05-13 16:01
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AiModelListener implements CommandLineRunner, ApplicationContextAware {

    ApplicationContext applicationContext;

    @Override
    public void run(String... args) {
        Map<String, ModelBuildHandler> beanMap = applicationContext.getBeansOfType(ModelBuildHandler.class);
        for (ModelBuildHandler modelBuildHandler : beanMap.values()) {
            MODEL_STRATEGY_MAP.put(modelBuildHandler.providerName(), modelBuildHandler);
        }
        aigcVectorStoreFactory.init();
        aigcModelFactory.init();
        aigcKnowledgeFactory.init();
        aigcAppFactory.init();
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private final AigcVectorStoreFactory aigcVectorStoreFactory;
    private final AigcModelFactory aigcModelFactory;
    private final AigcKnowledgeFactory aigcKnowledgeFactory;
    private final AigcAppFactory aigcAppFactory;

}
