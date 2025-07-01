package com.example.deal_service.model.mapper;

import com.example.deal_service.model.Deal;
import com.example.deal_service.model.DealContractor;
import com.example.deal_service.model.DealRequest;
import com.example.deal_service.model.DealSum;
import com.example.deal_service.model.dto.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        Deal deal = new Deal();

        deal.setDescription(dealDto.getDescription());
        deal.setAgreementNumber(dealDto.getAgreementNumber());
        deal.setAgreementDate(dealDto.getAgreementDate());
        deal.setAgreementStartDt(dealDto.getAgreementStartDt());
        deal.setAvailabilityDate(dealDto.getAvailabilityDate());
        deal.setCloseDt(dealDto.getCloseDt());

        return deal;
    }

    public static Deal dealRequestToEntity(DealRequest request) {
        if (request == null) {
            return null;
        }

        Deal deal = new Deal();

        deal.setDescription(request.getDescription());
        deal.setAgreementNumber(request.getAgreementNumber());
        deal.setAgreementDate(request.getAgreementDate());
        deal.setAgreementStartDt(request.getAgreementStartDt());
        deal.setAvailabilityDate(request.getAvailabilityDate());
        deal.setCloseDt(request.getCloseDt());

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
