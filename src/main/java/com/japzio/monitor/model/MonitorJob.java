package com.japzio.monitor.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.net.URL;
import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorJob {

    @NotNull
    private String endpoint;

    @NotNull
    private SupportedMethods method;

    private final Instant dateAdded = Instant.now();


}
