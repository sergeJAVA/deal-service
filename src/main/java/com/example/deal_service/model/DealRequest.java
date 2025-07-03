package com.example.deal_service.model;

import com.example.deal_service.model.dto.DealContractorDto;
import com.example.deal_service.model.dto.DealStatusDto;
import com.example.deal_service.model.dto.DealTypeDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Уникальный идентификатор сделки (для обновления).")
    private UUID id;

    /** Описание сделки. */
    @Schema(description = "Описание сделки.")
    private String description;

    /** Номер договора. */
    @Schema(description = "Номер договора.")
    private String agreementNumber;

    /** Дата заключения договора. */
    @Schema(description = "Дата заключения договора.")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate agreementDate;

    @Schema(description = "Дата и время начала действия договора.")
    /** Дата и время начала действия договора. */
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime agreementStartDt;

    @Schema(description = "Дата вступления в силу.")
    /** Дата вступления в силу. */
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate availabilityDate;

    @Schema(description = "Тип сделки.")
    /** Тип сделки. */
    private DealTypeDto type;

    @Schema(description = "Статус сделки.")
    /** Статус сделки. */
    private DealStatusDto status;

    @Schema(description = "Список сумм по сделке.")
    /** Список сумм по сделке. */
    private List<DealSumRequest> sum;

    @Schema(description = "Дата и время закрытия сделки.")
    /** Дата и время закрытия сделки. */
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime closeDt;
    @Schema(description = "Список контрагентов по сделке.")
    /** Список контрагентов по сделке. */
    private List<DealContractorDto> contractors;

}
