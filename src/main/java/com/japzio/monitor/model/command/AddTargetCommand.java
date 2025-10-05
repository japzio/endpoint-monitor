package com.japzio.monitor.model.command;

import com.japzio.monitor.model.dto.AddTargetRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTargetCommand {
    private AddTargetRequest request;
}
