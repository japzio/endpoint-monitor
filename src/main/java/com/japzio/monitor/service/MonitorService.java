package com.japzio.monitor.service;

import com.japzio.monitor.model.EndpointStatus;
import com.japzio.monitor.model.MonitorJob;
import com.japzio.monitor.model.command.AddTargetCommand;

import java.util.Map;
import java.util.Set;

public interface MonitorService {

    Set<MonitorJob> getTargets();
    void saveResults(Map<String, EndpointStatus> endpointStatus);
    Map<String, EndpointStatus> getResults();
    void saveNewTarget(AddTargetCommand command);
}
