package com.japzio.monitor.controller;

import com.japzio.monitor.model.command.AddTargetCommand;
import com.japzio.monitor.model.dto.AddTargetRequest;
import com.japzio.monitor.model.dto.AddTargetResponse;
import com.japzio.monitor.model.dto.GetAllTargetsCommand;
import com.japzio.monitor.model.dto.GetAllTargetsResponse;
import com.japzio.monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/v1")
public class MonitorController {

    public MonitorService monitorService;

    public MonitorController(
            @Autowired MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping("/targets")
    public GetAllTargetsResponse getTargets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return monitorService.getAllTargets(
                GetAllTargetsCommand.builder()
                        .page(page)
                        .size(size)
                        .build()
        );
    }

    @PostMapping("/targets")
    public AddTargetResponse addNewTarget(
            @RequestBody AddTargetRequest request
            ) {
        return monitorService.addNewTarget(AddTargetCommand.builder()
                        .request(request)
                .build());
    }

}
