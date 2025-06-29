package com.example.deal_service.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DealSumDto {

    private BigDecimal value;
    private String currency;

}
