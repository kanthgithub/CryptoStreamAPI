package com.cryptoStreamAPI.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.geo.CustomGeoModule;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.io.IOException;
import java.net.InetAddress;


@Configuration
@EnableElasticsearchRepositories(basePackages = "com.elasticDataLoader.repository")
@ComponentScan(basePackages = { "com.elasticDataLoader" })
public class ElasticSearchConfig {

    Logger log = LoggerFactory.getLogger(ElasticSearchConfig.class);

    @Value("${spring.data.elasticsearch.host}")
    private String esHost;

    @Value("${spring.data.elasticsearch.port}")
    private int esPort;

    @Value("${spring.data.elasticsearch.cluster-name}")
    private String esClusterName;

    @Bean
    public Client client() throws Exception {

       Settings esSettings = Settings.builder()
                .put("client.transport.sniff", true)
                .put("client.transport.ignore_cluster_name", true)
               .put("cluster.name",esClusterName)
               .put("http.enabled", true)
               .put("client.transport.ping_timeout", "60s")
               .put("transport.tcp.connect_timeout", "60s")
               .build();

        TransportClient client = new PreBuiltTransportClient(esSettings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esHost), esPort));
        return client;

    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
        log.info("elasticsearchTemplate building initiated");

        ElasticsearchTemplate elasticsearchTemplate =
                new ElasticsearchTemplate(client(), new CustomEntityMapper());
        log.info("elasticsearchTemplate build completed");

        return elasticsearchTemplate;
    }

    public static class CustomEntityMapper implements EntityMapper {

        private final ObjectMapper objectMapper;

        public CustomEntityMapper() {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            objectMapper.registerModule(new CustomGeoModule());
            objectMapper.registerModule(new JavaTimeModule());
        }

        @Override
        public String mapToString(Object object) throws IOException {
            return objectMapper.writeValueAsString(object);
        }

        @Override
        public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
            return objectMapper.readValue(source, clazz);
        }
    }
}
