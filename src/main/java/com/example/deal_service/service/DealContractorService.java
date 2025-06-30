package com.example.deal_service.service;

import com.example.deal_service.model.dto.DealContractorDto;

import java.util.UUID;

public interface DealContractorService {

    DealContractorDto saveDealContractor(DealContractorDto dealContractorDto);

    void deleteDealContractor(UUID id);

}
