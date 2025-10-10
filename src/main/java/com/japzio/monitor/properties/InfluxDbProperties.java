package com.japzio.monitor.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Data
@Configuration
@ConfigurationProperties(prefix = "influx")
public class InfluxDbProperties {

    private String url;
    private String token;
    private String org;
    private String bucket;

}
