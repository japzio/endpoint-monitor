package com.japzio.monitor.model.dto;

import com.japzio.monitor.entity.CheckResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckResultResponse {
    private UUID id;
    private UUID targetId;
    private String status;
    private Instant createAt;

    public static CheckResultResponse fromEntity(CheckResult checkResult) {
        return CheckResultResponse.builder()
                .id(checkResult.getId())
                .targetId(checkResult.getTargetId())
                .status(checkResult.getStatus())
                .createAt(checkResult.getCreatedAt().toInstant())
                .build();
    }
}
