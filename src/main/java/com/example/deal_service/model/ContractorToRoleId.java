package com.example.deal_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ContractorToRoleId {

    @Column(name = "contractor_id", nullable = false)
    private UUID contractorId;

    @Column(name = "role_id", length = 30, nullable = false)
    private String roleId;

}
