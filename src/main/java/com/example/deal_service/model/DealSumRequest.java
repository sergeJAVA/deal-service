package com.example.deal_service.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DealSumRequest {

    private BigDecimal value;
    private String currency;
    @Builder.Default
    private Boolean isMain = false;

}
