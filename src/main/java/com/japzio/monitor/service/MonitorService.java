package com.japzio.monitor.service;

import com.japzio.monitor.model.command.AddTargetCommand;
import com.japzio.monitor.model.command.GetAllCheckResultsCommand;
import com.japzio.monitor.model.command.GetAllTargetsCommand;
import com.japzio.monitor.model.external.*;

public interface MonitorService {

    AddTargetResponse addNewTarget(AddTargetCommand command);
    TargetResponse getTarget(String targetId);
    GetAllTargetsResponse getAllTargets(GetAllTargetsCommand command);
    void removeTarget(String targetId);

}
