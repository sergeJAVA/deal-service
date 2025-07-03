package com.internship.deal_service.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO для типа сделки.
 */
@Data
@Builder
public class DealTypeDto {

    /** Уникальный идентификатор типа сделки. */
    private String id;

    /** Наименование типа сделки. */
    private String name;

}
