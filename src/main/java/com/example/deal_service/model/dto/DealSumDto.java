package com.example.deal_service.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO для суммы сделки.
 */
@Data
@Builder
public class DealSumDto {

    /** Сумма сделки. */
    private BigDecimal value;
    /** Наименование валюты сделки. */
    private String currency;

}
