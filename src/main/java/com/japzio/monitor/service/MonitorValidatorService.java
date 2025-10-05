package com.japzio.monitor.service;

import com.japzio.monitor.model.command.AddTargetCommand;

import java.util.List;

public interface MonitorValidatorService {

    List<String> validate(AddTargetCommand command);

}
