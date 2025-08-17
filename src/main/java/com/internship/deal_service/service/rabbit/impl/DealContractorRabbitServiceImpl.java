package com.internship.deal_service.service.rabbit.impl;

import com.internship.deal_service.model.DealContractor;
import com.internship.deal_service.model.dto.ContractorRequestRabbit;
import com.internship.deal_service.model.dto.DealContractorDto;
import com.internship.deal_service.model.mapper.DealContractorMapper;
import com.internship.deal_service.repository.DealContractorRepository;
import com.internship.deal_service.service.rabbit.DealContractorRabbitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealContractorRabbitServiceImpl implements DealContractorRabbitService {

    private final DealContractorRepository dealContractorRepository;

    @Override
    @Transactional
    public DealContractorDto saveDealContractorWithUserId(ContractorRequestRabbit request) {
        List<DealContractor> dealContractorList = dealContractorRepository.findByContractorIdAndIsActiveTrue(request.getContractorId());
        Optional<DealContractor> existingDealContractor = dealContractorList
                .stream()
                .filter(dc -> dc.getContractorId().equals(request.getContractorId()))
                .findFirst();

        if (existingDealContractor.isPresent()) {

            DealContractor updatedDealContractor = existingDealContractor.get();
            updatedDealContractor.setContractorId(request.getContractorId());
            updatedDealContractor.setName(request.getName());
            updatedDealContractor.setModifyUserId(request.getModifyUserId());

            if (request.getInn() != null) {
                updatedDealContractor.setInn(request.getInn());
            }
            return DealContractorMapper.toDto(updatedDealContractor);

        } else {
            log.warn("DealContractor with contractorId <<{}>> doesn't exist or is not active.", request.getContractorId());
            return null;
        }
    }

}
