package com.japzio.monitor.model.dto;

import com.japzio.monitor.entity.Target;
import com.japzio.monitor.model.SupportedMethods;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetResponse {
    private UUID id;
    private String endpoint;
    private SupportedMethods method;
    private Timestamp createdAt;
    private Boolean enabled;

    public static TargetResponse fromEntity(Target target) {
        return TargetResponse.builder()
                .id(target.getId())
                .endpoint(target.getEndpoint())
                .method(SupportedMethods.valueOf(target.getMethod().name()))
                .createdAt(target.getCreatedAt())
                .enabled(target.getEnabled())
                .build();
    }
}
