package com.internship.deal_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для роли контрагента.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractorRoleDto {

    /** Уникальный идентификатор роли. */
    private String id;

    /** Наименование роли. */
    private String name;

    /** Категория роли. */
    private String category;

}
