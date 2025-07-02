package com.example.deal_service.model;

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
    private UUID dealId;
    /** ID нового статуса. */
    private String newStatusId;

}
