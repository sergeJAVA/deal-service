package com.internship.deal_service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "ID контрагента в сделке.", example = "93410138-aeb4-4058-a7ff-551570a15a08")
    private UUID dealContractorId;

    /** ID роли, которую нужно добавить/удалить. */
    @Schema(description = "ID роли, которую нужно добавить/удалить.", example = "WARRANTY")
    private String roleId;

}
