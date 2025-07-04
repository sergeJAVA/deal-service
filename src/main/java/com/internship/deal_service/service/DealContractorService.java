package com.internship.deal_service.service;

import com.internship.deal_service.model.dto.DealContractorRequest;
import com.internship.deal_service.model.dto.DealContractorDto;

import java.util.UUID;

public interface DealContractorService {

    DealContractorDto saveDealContractor(DealContractorRequest request);

    void deleteDealContractor(UUID id);

}
