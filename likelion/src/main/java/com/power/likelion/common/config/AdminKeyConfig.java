package com.power.likelion.common.config;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "pay.admin-key")
@Getter
@Component
public class AdminKeyConfig {
    private String adminKey;
}
