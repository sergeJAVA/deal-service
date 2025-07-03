package com.example.deal_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Запрос на добавление/удаление роли у контрагента.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractorRoleRequest {

    /** ID контрагента в сделке (DealContractor). */
    private UUID dealContractorId;

    /** ID роли, которую нужно добавить/удалить. */
    private String roleId;

}
