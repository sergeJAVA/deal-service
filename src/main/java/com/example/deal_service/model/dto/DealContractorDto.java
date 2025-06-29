package com.example.deal_service.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DealContractorDto {

    private UUID id;
    private String contractorId;
    private String name;
    private Boolean main;
    private String inn;
    private List<ContractorRoleDto> roles;

}
