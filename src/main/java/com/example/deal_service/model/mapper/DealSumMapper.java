package com.example.deal_service.model.mapper;

import com.example.deal_service.model.DealSum;
import com.example.deal_service.model.dto.DealSumDto;

/**
 * Утилитный класс-маппер для преобразования между сущностью DealSum и DTO.
 * Предоставляет статические методы для конвертации.
 */
public final class DealSumMapper {

    private DealSumMapper() {}

    public static DealSumDto toDto(DealSum dealSum) {
        if (dealSum == null) {
            return null;
        }
        return DealSumDto.builder()
                .value(dealSum.getSum())
                .currency(dealSum.getCurrency() != null ? dealSum.getCurrency().getId() : null)
                .build();
    }

    public static DealSum toEntity(DealSumDto dealSumDto) {
        if (dealSumDto == null) {
            return null;
        }
        DealSum dealSum = new DealSum();
        dealSum.setSum(dealSumDto.getValue());
        return dealSum;
    }

}
