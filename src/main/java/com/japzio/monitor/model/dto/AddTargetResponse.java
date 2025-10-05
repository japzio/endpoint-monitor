package com.japzio.monitor.model.dto;

import com.japzio.monitor.model.SupportedMethods;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTargetResponse {

    private String status;

}
