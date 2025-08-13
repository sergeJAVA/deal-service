package com.internship.deal_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO для контрагента в сделке.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealContractorDto {

    /** Уникальный идентификатор контрагента в сделке. */
    private UUID id;

    /** Идентификатор контрагента в системе. */
    private String contractorId;

    /** Наименование контрагента. */
    private String name;

    /** Признак основного контрагента по сделке. */
    private Boolean main;

    /** Список ролей данного контрагента в сделке. */
    private List<ContractorRoleDto> roles;

}
