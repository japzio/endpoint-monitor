package com.japzio.monitor.model.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckResultsDto {

    private UUID id;
    private UUID targetId;
    private String endpoint;
    private String method;
    private Boolean success;
    private Instant createdAt;
    private Integer duration;
    private String description;

}
