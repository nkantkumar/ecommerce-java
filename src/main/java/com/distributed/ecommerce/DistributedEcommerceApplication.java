package com.distributed.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@EnableAsync
@EnableTransactionManagement
@EnableRedisHttpSession
public class DistributedEcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DistributedEcommerceApplication.class, args);
    }
}