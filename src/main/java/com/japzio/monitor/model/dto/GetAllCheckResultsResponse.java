package com.japzio.monitor.model.dto;

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
public class GetAllCheckResultsResponse {

    List<CheckResultResponse> checkResults;
    Map<String, Object> metadata;

}
