package com.example.deal_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "contractor_to_role")
@Getter
@Setter
public class ContractorToRole {

    @EmbeddedId
    private ContractorToRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("contractorId")
    @JoinColumn(name = "contractor_id", referencedColumnName = "id", nullable = false)
    private DealContractor contractor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private ContractorRole role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public ContractorToRole() {

    }

    public ContractorToRole(DealContractor contractor, ContractorRole role) {
        this.contractor = contractor;
        this.role = role;
        this.id = new ContractorToRoleId(contractor.getId(), role.getId());
    }

}
