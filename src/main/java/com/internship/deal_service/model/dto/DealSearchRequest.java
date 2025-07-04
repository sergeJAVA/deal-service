package com.internship.deal_service.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Объект для передачи параметров поиска сделок.
 * Используется в качестве тела запроса в API для фильтрации сделок.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealSearchRequest {

    private UUID id;
    private String description;
    private String agreementNumber;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate agreementDateFrom;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate agreementDateTo;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate availabilityDateFrom;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate availabilityDateTo;

    private List<String> typeIds; // Список ID типов
    private List<String> statusIds; // Список ID статусов

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime closeDtFrom;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime closeDtTo;

    private String borrowerSearch; // Для поиска по контрагентам с ролью BORROWER
    private String warrantySearch; // Для поиска по контрагентам с ролью WARRANTY

    private BigDecimal sumValue;
    private String sumCurrency;

}
