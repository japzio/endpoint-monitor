package com.japzio.monitor.controller;

import com.japzio.monitor.model.command.AddTargetCommand;
import com.japzio.monitor.model.dto.AddTargetRequest;
import com.japzio.monitor.model.dto.AddTargetResponse;
import com.japzio.monitor.model.dto.GetAllCheckResultsCommand;
import com.japzio.monitor.model.dto.GetAllCheckResultsResponse;
import com.japzio.monitor.model.dto.GetAllTargetsCommand;
import com.japzio.monitor.model.dto.GetAllTargetsResponse;
import com.japzio.monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/v1/monitor")
public class MonitorController {

    public MonitorService monitorService;

    public MonitorController(
            @Autowired MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping("/targets")
    public GetAllTargetsResponse getTargets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "DESC") String order
    ) {
        return monitorService.getAllTargets(
                GetAllTargetsCommand.builder()
                        .order(order)
                        .page(page)
                        .size(size)
                        .build()
        );
    }

    @PostMapping("/targets")
    public ResponseEntity<AddTargetResponse> addNewTarget(
            @RequestBody AddTargetRequest request
            ) {
        var response = monitorService.addNewTarget(AddTargetCommand.builder()
                        .request(request)
                .build());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity
                .created(URI.create(""))
                .body(response);
    }

    @GetMapping("/targets/{target-id}/check-results")
    public GetAllCheckResultsResponse getTargetByIdCheckResults(
            @PathVariable("target-id") String targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "DESC") String order
    ) {
        return monitorService.getAllTargetCheckResults(
                GetAllCheckResultsCommand.builder()
                        .targetId(UUID.fromString(targetId))
                        .page(page)
                        .size(size)
                        .order(order)
                        .build()
        );
    }

}
