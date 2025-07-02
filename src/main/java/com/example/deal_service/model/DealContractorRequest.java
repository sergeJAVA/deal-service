package com.example.deal_service.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Запрос на создание/обновление контрагента в сделке.
 */
@Data
@Builder
public class DealContractorRequest {

    /** ID контрагента в сделке (для обновления). */
    private UUID id;

    /** ID сделки, к которой относится контрагент. */
    private UUID dealId;

    /** ID контрагента во внешней системе. */
    private String contractorId;

    /** Наименование контрагента. */
    private String name;

    /** Является ли контрагент основным. */
    private Boolean main;

    /** ИНН контрагента. */
    private String inn;

}
