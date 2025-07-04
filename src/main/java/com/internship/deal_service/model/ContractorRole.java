package com.internship.deal_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Справочная сущность для роли контрагента.
 * <p>
 * Хранит возможные роли, которые может выполнять контрагент в сделке (например, "Заемщик", "Поручитель").
 * </p>
 */
@Entity
@Table(name = "contractor_role")
@Getter
@Setter
public class ContractorRole {

    /**
     * Уникальный строковый идентификатор роли (например, "BORROWER").
     */
    @Id
    @Column(name = "id", length = 30, nullable = false)
    private String id;

    /**
     * Наименование роли (например, "Заемщик").
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Категория роли.
     */
    @Column(name = "category", length = 30, nullable = false)
    private String category;

    /**
     * Флаг активности. true - роль используется, false - архивная.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

}
