package com.example.deal_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.UUID;

/**
 * Составной первичный ключ для сущности {@link ContractorToRole}.
 * Используется для идентификации связи между контрагентом и ролью.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ContractorToRoleId {

    /**
     * ID контрагента в сделке (часть составного ключа).
     */
    @Column(name = "contractor_id", nullable = false)
    private UUID contractorId;

    /**
     * ID роли (часть составного ключа).
     */
    @Column(name = "role_id", length = 30, nullable = false)
    private String roleId;

}
