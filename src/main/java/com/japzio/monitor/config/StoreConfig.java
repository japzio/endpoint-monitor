package com.japzio.monitor.config;

import com.japzio.monitor.model.EndpointStatus;
import com.japzio.monitor.model.MonitorJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class StoreConfig {

    @Bean
    public Map<String, EndpointStatus> endpointStatusStorage(){
        return new HashMap<>();
    }

    @Bean
    public Set<MonitorJob> targetsStorage(){
        return new HashSet<>();
    }

}
