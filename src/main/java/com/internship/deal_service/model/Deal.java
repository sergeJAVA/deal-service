package com.internship.deal_service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Основная сущность, представляющая сделку.
 */
@Entity
@Table(name = "deal")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Deal {

    /**
     * Уникальный идентификатор сделки (UUID).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Описание сделки.
     */
    @Column(name = "description")
    private String description;

    /**
     * Номер договора.
     */
    @Column(name = "agreementNumber")
    private String agreementNumber;

    /**
     * Дата заключения договора.
     */
    @Column(name = "agreementDate")
    private LocalDate agreementDate;

    /**
     * Дата и время вступления соглашения в силу.
     */
    @Column(name = "agreement_start_dt")
    private LocalDateTime agreementStartDt;

    /**
     * Срок действия сделки.
     */
    @Column(name = "availability_date")
    private LocalDate availabilityDate;

    /**
     * Тип сделки.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", referencedColumnName = "id", nullable = false)
    private DealType type;

    /**
     * Статус сделки.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private DealStatus status;

    /**
     * Список сумм, связанных с этой сделкой.
     */
    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    @Builder.Default
    private List<DealSum> dealSums = new ArrayList<>();

    /**
     * Список контрагентов, участвующих в этой сделке.
     */
    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    @Builder.Default
    private List<DealContractor> dealContractors = new ArrayList<>();

    /**
     * Дата и время закрытия сделки.
     */
    @Column(name = "close_dt")
    private LocalDateTime closeDt;

    /**
     * Дата и время создания записи. Устанавливается автоматически.
     */
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    /**
     * Дата и время последнего изменения записи.
     */
    @Column(name = "modify_date")
    private LocalDateTime modifyDate;

    /**
     * Идентификатор пользователя, создавшего запись.
     */
    @Column(name = "create_user_id")
    private String createUserId;

    /**
     * Идентификатор пользователя, изменившего запись.
     */

    @Column(name = "modify_user_id")
    private String modifyUserId;

    /**
     * Флаг активности. true - сделка активна, false - удалена (архивна).
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Метод, вызываемый перед сохранением сущности для установки даты создания.
     */
    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    /**
     * Метод, вызываемый перед обновлением сущности для установки даты изменения.
     */
    @PreUpdate
    protected void onUpdate() {
        this.modifyDate = LocalDateTime.now();
    }

}
