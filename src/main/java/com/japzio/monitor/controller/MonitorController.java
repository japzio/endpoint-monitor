package com.japzio.monitor.controller;

import com.japzio.monitor.config.MonitorConfig;
import com.japzio.monitor.model.EndpointStatus;
import com.japzio.monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class MonitorController {

    public MonitorService monitorService;
    public MonitorConfig monitorConfig;

    public MonitorController(
            @Autowired MonitorService monitorService,
            @Autowired MonitorConfig monitorConfig) {
        this.monitorService = monitorService;
        this.monitorConfig = monitorConfig;
    }

    @GetMapping("/status")
    public Map<String, EndpointStatus> getEndpointStatuses() {
        return monitorService.getResults();
    }

    @GetMapping("/targets")
    public List<URL> getTargetEndpoints() {
        return monitorConfig.getTargetEndpoints();
    }

}
