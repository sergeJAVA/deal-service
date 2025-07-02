package com.example.deal_service.model.mapper;

import com.example.deal_service.model.Deal;
import com.example.deal_service.model.DealContractor;
import com.example.deal_service.model.DealRequest;
import com.example.deal_service.model.DealSum;
import com.example.deal_service.model.dto.DealContractorDto;
import com.example.deal_service.model.dto.DealDto;
import com.example.deal_service.model.dto.DealSumDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Утилитный класс-маппер для преобразования между сущностью Deal и DTO.
 * Предоставляет статические методы для конвертации.
 */
public final class DealMapper {

    private DealMapper() {

    }

    public static DealDto mapToDto(Deal deal) {
        if (deal == null) {
            return null;
        }

        DealDto.DealDtoBuilder builder = DealDto.builder()
                .id(deal.getId())
                .description(deal.getDescription())
                .agreementNumber(deal.getAgreementNumber())
                .agreementDate(deal.getAgreementDate())
                .agreementStartDt(deal.getAgreementStartDt())
                .availabilityDate(deal.getAvailabilityDate());

        if (deal.getType() != null) {
            builder.type(DealTypeMapper.toDto(deal.getType()));
        }
        if (deal.getStatus() != null) {
            builder.status(DealStatusMapper.toDto(deal.getStatus()));
        }

        builder.sum(mapMainDealSum(deal.getDealSums()));
        builder.closeDt(deal.getCloseDt());
        builder.contractors(mapDealContractors(deal.getDealContractors()));

        return builder.build();
    }

    public static Deal toEntity(DealDto dealDto) {
        if (dealDto == null) {
            return null;
        }

        Deal deal = Deal.builder()
                .description(dealDto.getDescription())
                .agreementNumber(dealDto.getAgreementNumber())
                .agreementDate(dealDto.getAgreementDate())
                .agreementStartDt(dealDto.getAgreementStartDt())
                .availabilityDate(dealDto.getAvailabilityDate())
                .closeDt(dealDto.getCloseDt())
                .build();

        return deal;
    }

    public static Deal dealRequestToEntity(DealRequest request) {
        if (request == null) {
            return null;
        }

        Deal deal = Deal.builder()
                .description(request.getDescription())
                .agreementNumber(request.getAgreementNumber())
                .agreementDate(request.getAgreementDate())
                .agreementStartDt(request.getAgreementStartDt())
                .availabilityDate(request.getAvailabilityDate())
                .closeDt(request.getCloseDt())
                .build();

        return deal;
    }

    private static DealSumDto mapMainDealSum(List<DealSum> dealSums) {
        if (dealSums == null || dealSums.isEmpty()) {
            return null;
        }
        return dealSums.stream()
                .filter(DealSum::getIsMain)
                .filter(DealSum::getIsActive)
                .findFirst()
                .map(DealSumMapper::toDto)
                .orElse(null);
    }

    private static List<DealContractorDto> mapDealContractors(List<DealContractor> dealContractors) {
        if (dealContractors == null || dealContractors.isEmpty()) {
            return Collections.emptyList();
        }
        return dealContractors.stream()
                .filter(DealContractor::getIsActive) // Фильтр по активным контрагентам
                .map(DealContractorMapper::toDto)
                .collect(Collectors.toList());
    }

}
