package com.japzio.monitor.model.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllTargetsResponse {

    List<TargetResponse> targets;
    Metadata metadata;

}
