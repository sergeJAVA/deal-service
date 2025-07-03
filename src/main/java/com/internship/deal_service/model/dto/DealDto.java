package com.internship.deal_service.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO для сделки.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealDto {

    /** Уникальный идентификатор сделки. */
    private UUID id;

    /** Описание сделки. */
    private String description;

    /** Номер договора. */
    private String agreementNumber;

    /** Дата заключения договора. */
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate agreementDate;

    /** Дата и время начала действия договора. */
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime agreementStartDt;

    /** Дата вступления в силу. */
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate availabilityDate;

    /** Тип сделки. */
    private DealTypeDto type;

    /** Статус сделки. */
    private DealStatusDto status;

    /** Основная сумма по сделке. */
    private DealSumDto sum;

    /** Дата и время закрытия сделки. */
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime closeDt;

    /** Список контрагентов по сделке. */
    private List<DealContractorDto> contractors;

}
