package com.japzio.monitor.model.dto;

import com.japzio.monitor.model.SupportedMethods;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.net.URL;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTargetRequest {

    @NotNull
    private String endpoint;

    @NotNull
    private SupportedMethods method;

}
