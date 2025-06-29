package com.example.deal_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "contractor_role")
@Getter
@Setter
public class ContractorRole {

    @Id
    @Column(name = "id", length = 30, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", length = 30, nullable = false)
    private String category;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

}
