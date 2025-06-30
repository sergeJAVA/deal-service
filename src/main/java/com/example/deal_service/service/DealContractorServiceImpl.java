package com.example.deal_service.service;

import com.example.deal_service.model.Deal;
import com.example.deal_service.model.DealContractor;
import com.example.deal_service.model.DealContractorRequest;
import com.example.deal_service.model.dto.DealContractorDto;
import com.example.deal_service.model.mapper.DealContractorMapper;
import com.example.deal_service.repository.ContractorRoleRepository;
import com.example.deal_service.repository.ContractorToRoleRepository;
import com.example.deal_service.repository.DealContractorRepository;
import com.example.deal_service.repository.DealRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DealContractorServiceImpl implements DealContractorService {

    private final DealContractorRepository dealContractorRepository;
    private final DealRepository dealRepository;
    private final ContractorRoleRepository contractorRoleRepository;
    private final ContractorToRoleRepository contractorToRoleRepository;

    @Override
    @Transactional
    public DealContractorDto saveDealContractor(DealContractorRequest request) {
        Deal deal = dealRepository.findByIdAndIsActiveTrue(request.getDealId())
                .orElseThrow(() -> new EntityNotFoundException("Deal с id " + request.getDealId() + " не найдена или неактивна."));


        if (request.getId() != null) {
            List<DealContractor> dealContractorList = dealContractorRepository.findAllByDealIdAndIsActiveTrue(request.getDealId());

            Optional<DealContractor> existingDealContractor = dealContractorList
                    .stream()
                    .filter(dc -> dc.getId().equals(request.getId()))
                    .findFirst();

            if (existingDealContractor.isPresent()) {
                DealContractor updatedDealContractor = existingDealContractor.get();
                updatedDealContractor.setDeal(deal);
                updatedDealContractor.setContractorId(request.getContractorId());
                updatedDealContractor.setName(request.getName());

                if (request.getInn() != null) {
                    updatedDealContractor.setInn(request.getInn());
                }

                if (request.getMain()) {
                    updatedDealContractor.setMain(Boolean.TRUE);
                    // реализовать дальнейшую логику, т.к только один контрагент может иметь main = true
                    dealContractorRepository.updateAllOthersMainToFalseForDeal(deal.getId(), updatedDealContractor.getId());
                }

                if (request.getRoles() != null || !request.getRoles().isEmpty()) {
                    // реализовать
                }
                return DealContractorMapper.toDto(updatedDealContractor);
            } else {
                throw new EntityNotFoundException("DealContractor с id " + request.getId() + " не найден или неактивен.");
            }
        }
        DealContractor dealContractor = new DealContractor();
        dealContractor.setDeal(deal);
        dealContractor.setContractorId(request.getContractorId());
        dealContractor.setName(request.getName());
        dealContractor.setCreateDate(LocalDateTime.now());
        if (request.getInn() != null) {
            dealContractor.setInn(request.getInn());
        }
        if (request.getMain()) {
            dealContractor.setMain(Boolean.TRUE);
        }
        if (request.getRoles() != null || !request.getRoles().isEmpty()) {
            // реализовать
        }
        DealContractor savedDealContractor = dealContractorRepository.save(dealContractor);

        return DealContractorMapper.toDto(savedDealContractor);
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
