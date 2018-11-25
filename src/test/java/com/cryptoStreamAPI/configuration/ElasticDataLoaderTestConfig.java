package com.cryptoStreamAPI.configuration;

import com.cryptoStreamAPI.common.YamlPropertyLoaderFactory;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@Configuration
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@EnableElasticsearchRepositories(basePackages = "com.cryptoStreamAPI")
@PropertySource(value = "classpath:application-test.yml", factory = YamlPropertyLoaderFactory.class)
@ComponentScan(basePackages = { "com.cryptoStreamAPI" }, excludeFilters = {
        @ComponentScan.Filter(type = ASSIGNABLE_TYPE,
                value = {
                        ElasticSearchConfig.class,
                        ElasticsearchAutoConfiguration.class
                })
})
@OverrideAutoConfiguration(enabled=true)
public class ElasticDataLoaderTestConfig extends ElasticSearchTestConfig{


}
