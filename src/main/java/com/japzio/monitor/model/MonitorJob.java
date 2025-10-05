package com.japzio.monitor.model;

import com.japzio.monitor.entity.CheckResult;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private CheckResult checkResult;

    private Instant dateAdded = Instant.now();

}
