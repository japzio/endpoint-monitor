package com.japzio.monitor.controller;

import com.japzio.monitor.model.command.AddTargetCommand;
import com.japzio.monitor.model.dto.*;
import com.japzio.monitor.service.MonitorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @GetMapping("/targets/{target-id}")
    public TargetResponse getTargets(
         @PathVariable("target-id") @Valid @org.hibernate.validator.constraints.UUID String targetId
    ) {
        return monitorService.getTarget(targetId);
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
                .created(location)
                .body(response);
    }

    @DeleteMapping("/targets/{target-id}")
    public ResponseEntity<?> deleteTarget(
            @PathVariable("target-id") @Valid @org.hibernate.validator.constraints.UUID String targetId
    ) {
        monitorService.removeTarget(targetId);
        return ResponseEntity.accepted().build();
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
