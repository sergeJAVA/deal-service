package com.internship.deal_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для статуса сделки.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealStatusDto {

    /** Уникальный идентификатор статуса сделки. */
    private String id;

    /** Наименование статуса сделки. */
    private String name;

}
