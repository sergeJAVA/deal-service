package com.internship.deal_service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Запрос на изменение статуса сделки.
 */
@Data
@Builder
public class DealStatusUpdateRequest {

    /** ID сделки, у которой меняется статус. */
    @Schema(description = "ID сделки, у которой меняется статус.", example = "93410138-aeb4-4058-a7ff-551570a15a08")
    private UUID dealId;

    /** ID нового статуса. */
    @Schema(description = "ID нового статуса.", example = "ACTIVE")
    private String newStatusId;

}
