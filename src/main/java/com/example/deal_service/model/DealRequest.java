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

@Data
@Builder
public class DealRequest {

    private UUID id;
    private String description;
    private String agreementNumber;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate agreementDate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime agreementStartDt;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate availabilityDate;

    private DealTypeDto type;
    private DealStatusDto status;
    private List<DealSumRequest> sum;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime closeDt;

    private List<DealContractorDto> contractors;

}
