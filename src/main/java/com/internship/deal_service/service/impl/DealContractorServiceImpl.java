package com.internship.deal_service.service.impl;

import com.internship.deal_service.exception.DealContractorException;
import com.internship.deal_service.model.Deal;
import com.internship.deal_service.model.DealContractor;
import com.internship.deal_service.model.dto.DealContractorRequest;
import com.internship.deal_service.model.dto.DealContractorDto;
import com.internship.deal_service.model.mapper.DealContractorMapper;
import com.internship.deal_service.repository.DealContractorRepository;
import com.internship.deal_service.repository.DealRepository;
import com.internship.deal_service.service.DealContractorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация сервиса {@link DealContractorService} для управления контрагентами в сделках.
 * <p>
 * Содержит логику для создания, обновления и "мягкого удаления" контрагентов,
 * привязанных к определенной сделке.
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DealContractorServiceImpl implements DealContractorService {

    private final DealContractorRepository dealContractorRepository;
    private final DealRepository dealRepository;

    @Override
    @Transactional
    public DealContractorDto saveDealContractor(DealContractorRequest request) {
        Deal deal = dealRepository.findByIdAndIsActiveTrue(request.getDealId())
                .orElseThrow(() -> new DealContractorException("Deal с id <<" + request.getDealId() + ">> не найдена или неактивна."));


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

                if (request.getMain() && updatedDealContractor.getMain().equals(Boolean.FALSE)) {
                    updatedDealContractor.setMain(Boolean.TRUE);
                    // реализовать дальнейшую логику, т.к только один контрагент у сделки может иметь main = true
                    dealContractorRepository.updateAllOthersMainToFalseForDeal(deal.getId(), updatedDealContractor.getId());
                } else if (request.getMain().equals(Boolean.FALSE) && updatedDealContractor.getMain().equals(Boolean.TRUE)) {
                    updatedDealContractor.setMain(Boolean.FALSE);
                }

                return DealContractorMapper.toDto(updatedDealContractor);
            } else {
                throw new DealContractorException("DealContractor с id <<" + request.getId() + ">> не найден или неактивен.");
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

        DealContractor savedDealContractor = dealContractorRepository.save(dealContractor);

        return DealContractorMapper.toDto(savedDealContractor);
    }

    @Override
    @Transactional
    public void deleteDealContractor(UUID id) {
        DealContractor dealContractor = dealContractorRepository.findById(id)
                .orElseThrow(() -> new DealContractorException("DealContractor с id <<" + id + ">> не найден."));

        if (dealContractor.getIsActive()) {
            dealContractor.setIsActive(false);
            dealContractor.setModifyDate(LocalDateTime.now());
            dealContractorRepository.save(dealContractor);
        }
    }

}
