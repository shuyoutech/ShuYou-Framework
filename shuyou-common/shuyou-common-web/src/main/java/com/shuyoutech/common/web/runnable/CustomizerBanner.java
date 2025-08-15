package com.shuyoutech.common.web.runnable;

import cn.hutool.core.thread.ThreadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author YangChao
 * @date 2025-07-01 11:12
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomizerBanner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        String name = environment.getProperty("spring.application.name");
        String osName = environment.getProperty("os.name");
        String osArch = environment.getProperty("os.arch");
        String osVersion = environment.getProperty("os.version");
        String javaVersion = environment.getProperty("java.version");
        String bootVersion = SpringBootVersion.getVersion();
        ThreadUtil.execute(() -> {
            log.info("操作系统：{},{},{}", osName, osArch, osVersion);
            log.info("Java环境：{}", javaVersion);
            log.info("SpringBoot版本：{}", bootVersion);
            log.info("项目启动成功！服务:{} ", name);
        });
    }

    private final Environment environment;
}
