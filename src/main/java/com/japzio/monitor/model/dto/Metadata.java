package com.japzio.monitor.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {

    private Integer currentPage;
    private Integer currentPageItems;
    private Integer totalPages;
    private Long totalElements;

}
