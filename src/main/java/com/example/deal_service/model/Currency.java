package com.example.deal_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность, представляющая валюту в рамках конкретной суммы.
 */
@Entity
@Table(name = "currency")
@Getter
@Setter
public class Currency {

    @Id
    @Column(name = "id", length = 3, nullable = false)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

}
