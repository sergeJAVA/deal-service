package com.example.deal_service.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DealStatusUpdateRequest {

    private UUID dealId;
    private String newStatusId;

}
