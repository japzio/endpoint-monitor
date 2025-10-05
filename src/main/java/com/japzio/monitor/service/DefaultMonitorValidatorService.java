package com.japzio.monitor.service;

import com.japzio.monitor.model.command.AddTargetCommand;
import com.japzio.monitor.model.dto.AddTargetRequest;
import com.japzio.monitor.validator.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DefaultMonitorValidatorService implements MonitorValidatorService {

    private final List<Validator<AddTargetRequest>> addTargetRequestValidatorList;

    public DefaultMonitorValidatorService(
            @Autowired
            @Qualifier("addTargetRequestValidatorList")
            List<Validator<AddTargetRequest>> addTargetRequestValidatorList) {
        this.addTargetRequestValidatorList = addTargetRequestValidatorList;
    }

    @Override
    public List<String> validate(AddTargetCommand command) {
        List<String> errors = new ArrayList<>();
        log.info("validators={}", addTargetRequestValidatorList.size());
        addTargetRequestValidatorList
                .forEach(
                        addTargetRequestValidator
                                -> addTargetRequestValidator.validate(command.getRequest(), errors)
                );
        return errors;
    }
}
