package com.example.deal_service.model.mapper;

import com.example.deal_service.model.ContractorRole;
import com.example.deal_service.model.ContractorToRole;
import com.example.deal_service.model.dto.ContractorRoleDto;

public final class ContractorRoleMapper {

    private ContractorRoleMapper() {}

    public static ContractorRoleDto toDto(ContractorRole contractorRole) {
        if (contractorRole == null) {
            return null;
        }
        return ContractorRoleDto.builder()
                .id(contractorRole.getId())
                .name(contractorRole.getName())
                .category(contractorRole.getCategory())
                .build();
    }

    public static ContractorRoleDto toDtoFromContractorToRole(ContractorToRole contractorToRole) {
        if (contractorToRole == null || !contractorToRole.getIsActive()) {
            return null;
        }

        return toDto(contractorToRole.getRole());
    }

    public static ContractorRole toEntity(ContractorRoleDto dto) {
        if (dto == null) {
            return null;
        }
        ContractorRole role = new ContractorRole();
        role.setId(dto.getId());
        role.setName(dto.getName());
        role.setCategory(dto.getCategory());
        return role;
    }

}
