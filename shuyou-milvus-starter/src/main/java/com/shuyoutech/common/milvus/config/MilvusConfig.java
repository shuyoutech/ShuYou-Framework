package com.shuyoutech.common.milvus.config;

import com.shuyoutech.common.milvus.properties.MilvusProperties;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author YangChao
 * @date 2025-08-21 17:04
 **/
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MilvusProperties.class)
public class MilvusConfig {

    @Bean
    public MilvusClientV2 milvusClientV2() {
        ConnectConfig connectConfig = ConnectConfig.builder() //
                .uri(milvusProperties.getUri()) //
                .username(milvusProperties.getUsername()) //
                .password(milvusProperties.getPassword()) //
                .build();
        return new MilvusClientV2(connectConfig);
    }

    private final MilvusProperties milvusProperties;

}
