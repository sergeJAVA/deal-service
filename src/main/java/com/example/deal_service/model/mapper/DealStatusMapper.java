package com.example.deal_service.model.mapper;

import com.example.deal_service.model.DealStatus;
import com.example.deal_service.model.dto.DealStatusDto;

public final class DealStatusMapper {

    private DealStatusMapper() {}

    public static DealStatusDto toDto(DealStatus dealStatus) {
        if (dealStatus == null) {
            return null;
        }
        return DealStatusDto.builder()
                .id(dealStatus.getId())
                .name(dealStatus.getName())
                .build();
    }

    public static DealStatus toEntity(DealStatusDto dealStatusDto) {
        if (dealStatusDto == null) {
            return null;
        }
        DealStatus dealStatus = new DealStatus();
        dealStatus.setId(dealStatusDto.getId());
        dealStatus.setName(dealStatusDto.getName());
        return dealStatus;
    }

}
