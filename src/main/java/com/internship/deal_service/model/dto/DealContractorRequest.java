package com.internship.deal_service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Запрос на создание/обновление контрагента в сделке.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealContractorRequest {

    /** ID контрагента в сделке (для обновления). */
    @Schema(description = "ID контрагента в сделке (для обновления).", example = "0e942205-95cc-427b-9251-7cb6f849b87e")
    private UUID id;

    /** ID сделки, к которой относится контрагент. */
    @Schema(description = "ID сделки, к которой относится контрагент.", example = "0e942205-95cc-427b-9251-7cb6f849b87e")
    private UUID dealId;

    /** ID контрагента во внешней системе. */
    @Schema(description = "ID контрагента во внешней системе.")
    private String contractorId;

    /** Наименование контрагента. */
    @Schema(description = "Наименование контрагента.")
    private String name;

    /** Является ли контрагент основным. */
    @Schema(description = "Является ли контрагент основным.")
    private Boolean main;

    /** ИНН контрагента. */
    @Schema(description = "ИНН контрагента.")
    private String inn;

}
