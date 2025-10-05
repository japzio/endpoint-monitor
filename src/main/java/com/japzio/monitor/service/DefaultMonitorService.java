package com.japzio.monitor.service;

import com.japzio.monitor.exception.AddNewTargetException;
import com.japzio.monitor.model.EndpointStatus;
import com.japzio.monitor.model.MonitorJob;
import com.japzio.monitor.model.command.AddTargetCommand;
import com.japzio.monitor.model.dto.AddTargetRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class DefaultMonitorService implements MonitorService {

    private final MonitorValidatorService monitorValidatorService;
    private final Map<String, EndpointStatus> endpointStatusStorage;
    private final Set<MonitorJob> targetsStorage;

    public DefaultMonitorService(
            @Autowired MonitorValidatorService monitorValidatorService,
            @Autowired Map<String, EndpointStatus> endpointStatusStore,
            @Autowired Set<MonitorJob> targetsStorage
    ) {
        this.monitorValidatorService = monitorValidatorService;
        this.endpointStatusStorage = endpointStatusStore;
        this.targetsStorage = targetsStorage;
    }

    @Override
    public void saveNewTarget(AddTargetCommand command) {
        log.info("action=saveNewTarget, info=validate");
        List<String> errors = this.monitorValidatorService.validate(command);
        if (!errors.isEmpty()) {
            throw new AddNewTargetException("Validation Error");
        }
        log.info("action=saveNewTarget");
        AddTargetRequest request = command.getRequest();
        targetsStorage.add(MonitorJob.builder()
                .endpoint(request.getEndpoint())
                .method(request.getMethod())
                .build());
    }

    @Override
    public Set<MonitorJob> getTargets() {
        log.info("action=getTargets");
        return targetsStorage;
    }

    @Override
    public void saveResults(Map<String, EndpointStatus> endpointStatus) {
        log.info("action=saveResults");
        this.endpointStatusStorage.putAll(endpointStatus);
    }

    @Override
    public Map<String, EndpointStatus> getResults() {
        log.info("action=getResults");
        return endpointStatusStorage;
    }

}
