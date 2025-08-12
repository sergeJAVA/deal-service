package com.internship.deal_service.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ContractorRequestRabbit {

    private String contractorId;

    private String name;

    private Boolean main;

    private String inn;

    private String createUserId;

    private String modifyUserId;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

}
