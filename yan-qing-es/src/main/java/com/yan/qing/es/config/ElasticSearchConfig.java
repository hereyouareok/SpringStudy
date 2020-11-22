package com.yan.qing.es.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author YanQin
 * @version v1.0.0
 * @Description : Elasticsearch配置类
 * @Create on : 2020/11/22 16:44
 **/
@Configuration
public class ElasticSearchConfig {

    @Bean
    RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.182.136", 9200, "http")));
        return client;
    }
}
