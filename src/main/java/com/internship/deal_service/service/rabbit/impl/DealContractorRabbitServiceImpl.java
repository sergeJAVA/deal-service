package com.internship.deal_service.service.rabbit.impl;

import com.internship.deal_service.exception.DealContractorException;
import com.internship.deal_service.model.Deal;
import com.internship.deal_service.model.DealContractor;
import com.internship.deal_service.model.dto.ContractorRequestRabbit;
import com.internship.deal_service.model.dto.DealContractorDto;
import com.internship.deal_service.model.mapper.DealContractorMapper;
import com.internship.deal_service.repository.DealContractorRepository;
import com.internship.deal_service.repository.DealRepository;
import com.internship.deal_service.service.rabbit.DealContractorRabbitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DealContractorRabbitServiceImpl implements DealContractorRabbitService {

    private final DealContractorRepository dealContractorRepository;
    private final DealRepository dealRepository;

    @Override
    @Transactional
    public DealContractorDto saveDealContractorWithUserId(ContractorRequestRabbit request) {
        Optional<Deal> deal = Optional.empty();

        List<DealContractor> dealContractorList = dealContractorRepository.findByContractorIdAndIsActiveTrue(request.getContractorId());
        Optional<DealContractor> existingDealContractor = dealContractorList
                .stream()
                .filter(dc -> dc.getContractorId().equals(request.getContractorId()))
                .findFirst();

        if (existingDealContractor.isPresent()) {
            Deal existingDeal = existingDealContractor.get().getDeal();
            if (existingDeal != null) {
                deal = dealRepository.findByIdAndIsActiveTrue(existingDeal.getId());
            } else {
                throw new DealContractorException("Deal с id <<" + existingDeal.getId() + ">> не найдена или неактивна.");
            }
        }

        if (deal.isPresent() && existingDealContractor.isPresent()) {

            DealContractor updatedDealContractor = existingDealContractor.get();
            updatedDealContractor.setDeal(deal.get());
            updatedDealContractor.setContractorId(request.getContractorId());
            updatedDealContractor.setName(request.getName());
            updatedDealContractor.setModifyUserId(request.getModifyUserId());

            if (request.getInn() != null) {
                updatedDealContractor.setInn(request.getInn());
            }

            if (request.getMain() && updatedDealContractor.getMain().equals(Boolean.FALSE)) {

                updatedDealContractor.setMain(Boolean.TRUE);
                dealContractorRepository.updateAllOthersMainToFalseForDeal(deal.get().getId(), updatedDealContractor.getId());

            } else if (request.getMain().equals(Boolean.FALSE) && updatedDealContractor.getMain().equals(Boolean.TRUE)) {
                updatedDealContractor.setMain(Boolean.FALSE);
            }

            return DealContractorMapper.toDto(updatedDealContractor);

        } else if (existingDealContractor.isPresent()) {

            DealContractor updatedDealContractor = existingDealContractor.get();
            updatedDealContractor.setContractorId(request.getContractorId());
            updatedDealContractor.setName(request.getName());
            updatedDealContractor.setModifyUserId(request.getModifyUserId());
            updatedDealContractor.setMain(request.getMain());

            if (request.getInn() != null) {
                updatedDealContractor.setInn(request.getInn());
            }

            return DealContractorMapper.toDto(updatedDealContractor);
        } else {
            DealContractor dealContractor = getDealContractor(request, deal);

            DealContractor savedDealContractor = dealContractorRepository.save(dealContractor);

            return DealContractorMapper.toDto(savedDealContractor);
        }

    }

    private static DealContractor getDealContractor(ContractorRequestRabbit request, Optional<Deal> deal) {
        DealContractor dealContractor = new DealContractor();
        deal.ifPresent(dealContractor::setDeal);
        dealContractor.setContractorId(request.getContractorId());
        dealContractor.setName(request.getName());
        dealContractor.setCreateDate(request.getCreateDate());
        dealContractor.setCreateUserId(request.getCreateUserId());
        if (request.getInn() != null) {
            dealContractor.setInn(request.getInn());
        }
        if (request.getMain()) {
            dealContractor.setMain(Boolean.TRUE);
        }
        return dealContractor;
    }

}
