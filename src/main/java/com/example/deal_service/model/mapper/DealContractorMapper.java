package com.example.deal_service.model.mapper;

import com.example.deal_service.model.ContractorToRole;
import com.example.deal_service.model.DealContractor;
import com.example.deal_service.model.dto.ContractorRoleDto;
import com.example.deal_service.model.dto.DealContractorDto;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * Утилитный класс-маппер для преобразования между сущностью DealContractor и DTO.
 * Предоставляет статические методы для конвертации.
 */
public final class DealContractorMapper {

    private DealContractorMapper() {}

    public static DealContractorDto toDto(DealContractor dealContractor) {
        if (dealContractor == null) {
            return null;
        }
        DealContractorDto.DealContractorDtoBuilder builder = DealContractorDto.builder()
                .id(dealContractor.getId())
                .contractorId(dealContractor.getContractorId())
                .name(dealContractor.getName())
                .main(dealContractor.getMain());

        builder.roles(mapContractorRoles(dealContractor.getRoles()));

        return builder.build();
    }

    private static List<ContractorRoleDto> mapContractorRoles(Set<ContractorToRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream()
                .filter(ContractorToRole::getIsActive)
                .map(ContractorRoleMapper::toDtoFromContractorToRole)
                .collect(Collectors.toList());
    }

}
