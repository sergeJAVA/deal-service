package com.example.deal_service.service;

import com.example.deal_service.model.DealContractor;
import com.example.deal_service.model.dto.DealContractorDto;
import com.example.deal_service.repository.ContractorRoleRepository;
import com.example.deal_service.repository.ContractorToRoleRepository;
import com.example.deal_service.repository.DealContractorRepository;
import com.example.deal_service.repository.DealRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DealContractorServiceImpl implements DealContractorService {

    private final DealContractorRepository dealContractorRepository;
    private final DealRepository dealRepository;
    private final ContractorRoleRepository contractorRoleRepository;
    private final ContractorToRoleRepository contractorToRoleRepository;

    @Override
    @Transactional
    public DealContractorDto saveDealContractor(DealContractorDto dto) {

    }

    @Override
    @Transactional
    public void deleteDealContractor(UUID id) {
        DealContractor dealContractor = dealContractorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DealContractor с id " + id + " не найден."));

        if (dealContractor.getIsActive()) {
            dealContractor.setIsActive(false);
            dealContractor.setModifyDate(LocalDateTime.now());
            dealContractorRepository.save(dealContractor);
        }
    }

}
