package com.japzio.monitor.service;

import com.japzio.monitor.entity.Target;
import com.japzio.monitor.exception.AddNewTargetException;
import com.japzio.monitor.model.command.AddTargetCommand;
import com.japzio.monitor.model.dto.*;
import com.japzio.monitor.repository.TargetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DefaultMonitorService implements MonitorService {

    private final MonitorValidatorService monitorValidatorService;
    private final TargetRepository targetRepository;

    public DefaultMonitorService(
            @Autowired MonitorValidatorService monitorValidatorService,
            @Autowired TargetRepository targetRepository
    ) {
        this.monitorValidatorService = monitorValidatorService;
        this.targetRepository = targetRepository;
    }

    public GetAllTargetsResponse getAllTargets(GetAllTargetsCommand command) {

        var pageRequest = PageRequest.of(command.getPage(), command.getSize());

        Page<Target> result = targetRepository.findAll(pageRequest);

        return GetAllTargetsResponse.builder()
                .targets(
                        result.getContent().stream()
                                .map(TargetResponse::fromEntity)
                                .toList()
                )
                .metadata(
                        Map.of(
                                "currentPage", result.getNumber(),
                                "totalPages", result.getTotalPages(),
                                "totalElements", result.getTotalElements()
                        )
                )
                .build();
    }

    @Override
    public AddTargetResponse addNewTarget(AddTargetCommand command) {
        log.info("action=saveNewTarget, info=validate");
        List<String> errors = this.monitorValidatorService.validate(command);
        if (!errors.isEmpty()) {
            throw new AddNewTargetException("Validation Error");
        }
        log.info("action=saveNewTarget");
        AddTargetRequest request = command.getRequest();
        targetRepository.save(Target.builder()
                .endpoint(request.getEndpoint())
                .method(request.getMethod())
                .createdAt(Timestamp.from(Instant.now()))
                .build());

        return AddTargetResponse.builder()
                .status("OK")
                .build();

    }

}
