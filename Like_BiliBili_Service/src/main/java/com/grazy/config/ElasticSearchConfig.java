package com.grazy.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * @Author: grazy
 * @Date: 2023/9/23 0:22
 * @Description: ElasticSearch配置类
 */

@Configuration
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

    @Value("${elasticsearch.url}")
    private String esUrl;


    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(esUrl)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
