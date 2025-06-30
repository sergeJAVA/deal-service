package com.example.deal_service.service;

import com.example.deal_service.model.DealContractorRequest;
import com.example.deal_service.model.dto.DealContractorDto;

import java.util.UUID;

public interface DealContractorService {

    DealContractorDto saveDealContractor(DealContractorRequest request);

    void deleteDealContractor(UUID id);

}
