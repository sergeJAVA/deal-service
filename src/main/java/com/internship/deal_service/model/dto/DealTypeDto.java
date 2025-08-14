package com.internship.deal_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для типа сделки.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealTypeDto {

    /** Уникальный идентификатор типа сделки. */
    private String id;

    /** Наименование типа сделки. */
    private String name;

}
