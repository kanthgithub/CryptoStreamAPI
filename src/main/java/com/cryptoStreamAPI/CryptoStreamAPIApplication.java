package com.cryptoStreamAPI;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableAutoConfiguration(exclude= { ElasticsearchAutoConfiguration.class })
public class CryptoStreamAPIApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoStreamAPIApplication.class, args);
    }


}
