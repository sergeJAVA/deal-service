package com.internship.deal_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность, реализующая связь "многие-ко-многим" между контрагентом в сделке ({@link DealContractor})
 * и его ролью ({@link ContractorRole}).
 */
@Entity
@Table(name = "contractor_to_role")
@Getter
@Setter
public class ContractorToRole {

    /**
     * Составной первичный ключ.
     */
    @EmbeddedId
    private ContractorToRoleId id;

    /**
     * Контрагент, которому принадлежит роль.
     * {@link MapsId} указывает, что поле {@code contractorId} из {@link ContractorToRoleId}
     * используется для маппинга этой связи.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("contractorId")
    @JoinColumn(name = "contractor_id", referencedColumnName = "id", nullable = false)
    private DealContractor contractor;

    /**
     * Роль, назначенная контрагенту.
     * {@link MapsId} указывает, что поле {@code roleId} из {@link ContractorToRoleId}
     * используется для маппинга этой связи.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private ContractorRole role;

    /**
     * Флаг активности связи. true - связь активна, false - неактивна.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Конструктор по умолчанию, необходимый для JPA.
     */
    public ContractorToRole() {

    }

    /**
     * Конструктор для создания связи между контрагентом и ролью.
     * Автоматически создает составной ключ.
     *
     * @param contractor Контрагент сделки.
     * @param role       Роль контрагента.
     */
    public ContractorToRole(DealContractor contractor, ContractorRole role) {
        this.contractor = contractor;
        this.role = role;
        this.id = new ContractorToRoleId(contractor.getId(), role.getId());
    }

}
