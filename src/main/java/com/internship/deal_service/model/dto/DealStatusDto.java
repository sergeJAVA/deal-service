package com.internship.deal_service.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO для статуса сделки.
 */
@Data
@Builder
public class DealStatusDto {

    /** Уникальный идентификатор статуса сделки. */
    private String id;

    /** Наименование статуса сделки. */
    private String name;

}
