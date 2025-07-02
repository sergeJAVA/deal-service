package com.example.deal_service.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO для роли контрагента.
 */
@Data
@Builder
public class ContractorRoleDto {

    /** Уникальный идентификатор роли. */
    private String id;

    /** Наименование роли. */
    private String name;

    /** Категория роли. */
    private String category;

}
