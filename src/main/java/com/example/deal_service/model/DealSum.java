package com.example.deal_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "deal_sum")
@Getter
@Setter
public class DealSum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id", referencedColumnName = "id", nullable = false)
    private Deal deal;

    @Column(name = "sum", precision = 100, scale = 2, nullable = false)
    private BigDecimal sum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", referencedColumnName = "id", nullable = false)
    private Currency currency;

    @Column(name = "is_main", nullable = false)
    private Boolean isMain = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

}
