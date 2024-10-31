package com.japzio.monitor.service;

import com.japzio.monitor.model.EndpointStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DefaultMonitorService implements MonitorService {

    public Map<String, EndpointStatus> endpointStatusStore;

    public DefaultMonitorService(
            @Autowired Map<String, EndpointStatus> endpointStatusStore
    ) {
        this.endpointStatusStore = endpointStatusStore;
    }

    @Override
    public void saveResults(Map<String, EndpointStatus> endpointStatus) {
        this.endpointStatusStore.putAll(endpointStatus);
    }

    @Override
    public Map<String, EndpointStatus> getResults() {
        return endpointStatusStore;
    }
}
