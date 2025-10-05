package com.japzio.monitor.service;

import com.japzio.monitor.model.command.AddTargetCommand;
import com.japzio.monitor.model.dto.*;

public interface MonitorService {

    GetAllTargetsResponse getAllTargets(GetAllTargetsCommand command);
    AddTargetResponse addNewTarget(AddTargetCommand command);
    GetAllCheckResultsResponse getAllTargetCheckResults(GetAllCheckResultsCommand command);

}
