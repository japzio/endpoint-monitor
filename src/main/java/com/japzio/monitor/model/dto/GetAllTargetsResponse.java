package com.japzio.monitor.model.dto;

import com.japzio.monitor.entity.Target;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllTargetsResponse {

    List<TargetResponse> targets;
    Map<String, Object> metadata;

}
