package com.japzio.monitor.service;

import com.japzio.monitor.entity.CheckResult;
import com.japzio.monitor.entity.Target;
import com.japzio.monitor.exception.AddNewTargetException;
import com.japzio.monitor.exception.TargetNotFoundException;
import com.japzio.monitor.model.command.AddTargetCommand;
import com.japzio.monitor.model.dto.AddTargetRequest;
import com.japzio.monitor.model.dto.AddTargetResponse;
import com.japzio.monitor.model.dto.CheckResultResponse;
import com.japzio.monitor.model.dto.GetAllCheckResultsCommand;
import com.japzio.monitor.model.dto.GetAllCheckResultsResponse;
import com.japzio.monitor.model.dto.GetAllTargetsCommand;
import com.japzio.monitor.model.dto.GetAllTargetsResponse;
import com.japzio.monitor.model.dto.Metadata;
import com.japzio.monitor.model.dto.TargetResponse;
import com.japzio.monitor.repository.CheckResultRepository;
import com.japzio.monitor.repository.TargetRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class DefaultMonitorService implements MonitorService {

    private static final String TARGETS_ODER_FIELD = "createdAt";
    private static final String CHECK_RESULTS_ODER_FIELD = "createdAt";

    private final MonitorValidatorService monitorValidatorService;
    private final TargetRepository targetRepository;
    private final CheckResultRepository checkResultRepository;

    public DefaultMonitorService(
            @Autowired MonitorValidatorService monitorValidatorService,
            @Autowired TargetRepository targetRepository,
            @Autowired CheckResultRepository checkResultRepository
    ) {
        this.monitorValidatorService = monitorValidatorService;
        this.targetRepository = targetRepository;
        this.checkResultRepository = checkResultRepository;
    }

    @Override
    public TargetResponse getTarget(String targetId) {

        var target = targetRepository.findById(UUID.fromString(targetId))
                .orElseThrow(() -> new TargetNotFoundException(targetId));

        return  TargetResponse.fromEntity(target);

    }

    public GetAllTargetsResponse getAllTargets(GetAllTargetsCommand command) {

        var pageRequest = PageRequest.of(
                command.getPage(),
                command.getSize(),
                Sort.by(
                        Sort.Direction.valueOf(command.getOrder()),
                        TARGETS_ODER_FIELD
                )
        );

        Page<Target> result = targetRepository.findAll(pageRequest);

        return GetAllTargetsResponse.builder()
                .targets(
                        result.getContent().stream()
                                .map(TargetResponse::fromEntity)
                                .toList()
                )
                .metadata(
                        Metadata.builder()
                                .currentPage(result.getNumber())
                                .currentPageItems(result.getContent().size())
                                .totalPages(result.getTotalPages())
                                .totalElements(result.getTotalElements())
                                .build()
                ).build();
    }

    @Override
    @Transactional
    public void removeTarget(String targetId) {
        log.info("action=removeTarget, info=checkBeforeRemoving, targetId={}", targetId);
        if(!targetRepository.existsById(UUID.fromString(targetId))) {
            log.error("action=removeTarget, error=deleteTargetFailed, targetId={}", targetId);
            throw new TargetNotFoundException("Attempting to delete non existent targetId" + targetId);
        }
        log.info("action=removeTarget, info=removeTarget, targetId={}", targetId);
        targetRepository.deleteById(UUID.fromString(targetId));
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

        var target = Target.builder()
                .endpoint(request.getEndpoint())
                .enabled(request.getEnabled())
                .method(request.getMethod())
                .createdAt(Timestamp.from(Instant.now()))
                .build();

        targetRepository.save(target);

        return AddTargetResponse.builder()
                .id(target.getId().toString())
                .build();
    }

    @Override
    public GetAllCheckResultsResponse getAllTargetCheckResults(GetAllCheckResultsCommand command) {

        var pageRequest = PageRequest.of(
                command.getPage(),
                command.getSize(),
                Sort.by(
                        Sort.Direction.valueOf(command.getOrder()),
                        CHECK_RESULTS_ODER_FIELD
                )
        );

        Page<CheckResult> result = checkResultRepository.findAllByTargetId(command.getTargetId(), pageRequest);

        return GetAllCheckResultsResponse.builder()
                .checkResults(
                        result.getContent().stream()
                                .map(CheckResultResponse::fromEntity)
                                .toList()
                )
                .metadata(
                        Metadata.builder()
                                .currentPage(result.getNumber())
                                .currentPageItems(result.getContent().size())
                                .totalPages(result.getTotalPages())
                                .totalElements(result.getTotalElements())
                                .build()
                )
                .build();

    }

}
