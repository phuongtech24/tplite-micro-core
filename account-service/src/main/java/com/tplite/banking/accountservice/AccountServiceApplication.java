package com.tplite.banking.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = {"com.tplite.banking"})
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableCaching
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
