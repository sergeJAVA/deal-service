package com.example.deal_service.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractorRoleDto {

    private String id;
    private String name;
    private String category;

}
