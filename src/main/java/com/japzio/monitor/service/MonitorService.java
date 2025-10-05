package com.japzio.monitor.service;

import com.japzio.monitor.model.command.AddTargetCommand;
import com.japzio.monitor.model.dto.AddTargetResponse;
import com.japzio.monitor.model.dto.GetAllCheckResultsCommand;
import com.japzio.monitor.model.dto.GetAllCheckResultsResponse;
import com.japzio.monitor.model.dto.GetAllTargetsCommand;
import com.japzio.monitor.model.dto.GetAllTargetsResponse;

public interface MonitorService {

    GetAllTargetsResponse getAllTargets(GetAllTargetsCommand command);
    AddTargetResponse addNewTarget(AddTargetCommand command);
    GetAllCheckResultsResponse getAllTargetCheckResults(GetAllCheckResultsCommand command);

}
