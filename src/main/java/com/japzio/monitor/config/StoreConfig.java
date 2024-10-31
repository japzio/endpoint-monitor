package com.japzio.monitor.config;

import com.japzio.monitor.model.EndpointStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StoreConfig {
    @Bean
    public Map<String, EndpointStatus> endpointStatusStore(){
        return new HashMap<>();
    }
}
