package com.japzio.monitor.properties;

import lombok.Data;

@Data
public class MonitorProperties {

    private String cronExpression;
    private Long maxTimeout;

}
