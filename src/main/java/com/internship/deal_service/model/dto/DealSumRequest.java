package com.internship.deal_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealSumRequest {

    private BigDecimal value;
    private String currency;
    @Builder.Default
    private Boolean isMain = false;

}
