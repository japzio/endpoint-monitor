package com.japzio.monitor.model.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllCheckResultsCommand {
    private UUID targetId;
    private Integer size;
    private Integer page;
    private String order;
}
