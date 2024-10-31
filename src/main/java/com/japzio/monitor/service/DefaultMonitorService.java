package com.japzio.monitor.service;

import com.japzio.monitor.model.EndpointStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DefaultMonitorService implements MonitorService {

    public Map<String, EndpointStatus> endpointStatusStore;

    public DefaultMonitorService(Map<String, EndpointStatus> endpointStatusStore) {
        this.endpointStatusStore = endpointStatusStore;
    }

    @Override
    public void saveResults(List<EndpointStatus> endpointStatusList) {

    }

    @Override
    public Map<String, EndpointStatus> getResults() {
        return endpointStatusStore;
    }
}
