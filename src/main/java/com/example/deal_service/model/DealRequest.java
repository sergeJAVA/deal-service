package com.example.deal_service.model;

import com.example.deal_service.model.dto.DealContractorDto;
import com.example.deal_service.model.dto.DealStatusDto;
import com.example.deal_service.model.dto.DealTypeDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Запрос на создание/обновление сделки.
 */
@Data
@Builder
public class DealRequest {

    /** Уникальный идентификатор сделки (для обновления). */
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

    /** Список сумм по сделке. */
    private List<DealSumRequest> sum;

    /** Дата и время закрытия сделки. */
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime closeDt;

    /** Список контрагентов по сделке. */
    private List<DealContractorDto> contractors;

}
