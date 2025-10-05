package com.japzio.monitor.controller;

import com.japzio.monitor.model.command.AddTargetCommand;
import com.japzio.monitor.model.dto.*;
import com.japzio.monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @GetMapping("/targets/{target-id}/check-results")
    public GetAllCheckResultsResponse getTargetByIdCheckResults(
            @PathVariable("target-id") String targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return monitorService.getAllTargetCheckResults(
                GetAllCheckResultsCommand.builder()
                        .targetId(UUID.fromString(targetId))
                        .page(page)
                        .size(size)
                        .build()
        );
    }

}
