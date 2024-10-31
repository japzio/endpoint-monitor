package com.japzio.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "executor")
public class MonitorConfig {

    private List<URL> targetEndpoints;

}
