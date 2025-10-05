package com.japzio.monitor.model.dto;

import com.japzio.monitor.model.SupportedMethods;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTargetRequest {

    @NotNull
    private String endpoint;

    @NotNull
    private SupportedMethods method;

    private Boolean enabled;

}
