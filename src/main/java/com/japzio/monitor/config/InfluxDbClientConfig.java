package com.japzio.monitor.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.japzio.monitor.properties.InfluxDbProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDbClientConfig {



    @Bean
    public InfluxDBClient influxDBClient(@Autowired InfluxDbProperties influxDbProperties) {
        return InfluxDBClientFactory
                .create(
                        influxDbProperties.getUrl(),
                        influxDbProperties.getToken().toCharArray(),
                        influxDbProperties.getOrg(),
                        influxDbProperties.getBucket()
                );
    }
}