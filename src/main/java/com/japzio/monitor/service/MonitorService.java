package com.japzio.monitor.service;

import com.japzio.monitor.model.EndpointStatus;

import java.util.Map;

public interface MonitorService {
    void saveResults(Map<String, EndpointStatus> endpointStatus);
    Map<String, EndpointStatus> getResults();
}
