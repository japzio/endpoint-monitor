package com.japzio.monitor.config;

import com.japzio.monitor.properties.MonitorProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    @ConfigurationProperties(prefix = "monitor")
    public MonitorProperties monitorProperties() {
        return new MonitorProperties();
    }

}
