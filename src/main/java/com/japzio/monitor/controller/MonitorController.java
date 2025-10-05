package com.japzio.monitor.controller;

import com.japzio.monitor.model.EndpointStatus;
import com.japzio.monitor.model.MonitorJob;
import com.japzio.monitor.model.command.AddTargetCommand;
import com.japzio.monitor.model.dto.AddTargetRequest;
import com.japzio.monitor.model.dto.AddTargetResponse;
import com.japzio.monitor.service.MonitorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@Validated
@RequestMapping("/v1")
public class MonitorController {

    public MonitorService monitorService;

    public MonitorController(
            @Autowired MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @PostMapping("/targets")
    public AddTargetResponse addTarget(
            @RequestBody @Valid AddTargetRequest request
    ) {
        monitorService.saveNewTarget(AddTargetCommand.builder()
                        .request(request)
                .build());
        return AddTargetResponse.builder()
                .status("Ok")
                .build();
    }

    @GetMapping("/targets")
    public Set<MonitorJob> getTargets() {
        return monitorService.getTargets();
    }

    @GetMapping("/statuses")
    public Map<String, EndpointStatus> getEndpointStatuses() {
        return monitorService.getResults();
    }

}
