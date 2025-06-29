package com.example.deal_service.model.mapper;

import com.example.deal_service.model.DealType;
import com.example.deal_service.model.dto.DealTypeDto;

public final class DealTypeMapper {

    private DealTypeMapper() {}

    public static DealTypeDto toDto(DealType dealType) {
        if (dealType == null) {
            return null;
        }
        return DealTypeDto.builder()
                .id(dealType.getId())
                .name(dealType.getName())
                .build();
    }

    public static DealType toEntity(DealTypeDto dealTypeDto) {
        if (dealTypeDto == null) {
            return null;
        }
        DealType dealType = new DealType();
        dealType.setId(dealTypeDto.getId());
        dealType.setName(dealTypeDto.getName());
        return dealType;
    }

}
