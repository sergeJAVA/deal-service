package com.example.deal_service.service;

import com.example.deal_service.model.*;
import com.example.deal_service.model.dto.DealDto;
import com.example.deal_service.model.dto.DealSumDto;
import com.example.deal_service.model.mapper.DealMapper;
import com.example.deal_service.repository.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final DealTypeRepository dealTypeRepository;
    private final DealStatusRepository dealStatusRepository;
    private final DealSumRepository dealSumRepository;
    private final CurrencyRepository currencyRepository;

    @Override
    @Transactional(readOnly = true)
    public DealDto getDealById(UUID id) {
        Deal deal = dealRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Deal с id " + id + " не найдена или неактивна"));
        return DealMapper.mapToDto(deal);
    }

    @Override
    @Transactional
    public DealDto saveDeal(DealDto dealDto) {
        DealType dealType = dealTypeRepository.findByIdAndIsActiveTrue(dealDto.getType().getId())
                .orElseThrow(() -> new EntityNotFoundException("DealType с id " + dealDto.getType().getId() + " не был найден или неактивен."));

        DealStatus dealStatusDraft = dealStatusRepository.findByIdAndIsActiveTrue("DRAFT")
                .orElseThrow(() -> new EntityNotFoundException("DealStatus \"DRAFT\" не был найден или неактивен."));

        Deal deal;
        if (dealDto.getId() != null) {
            deal = dealRepository.findByIdAndIsActiveTrue(dealDto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Deal с id " + dealDto.getId() + " не найдена или неактивна для обновления."));
            deal.setDescription(dealDto.getDescription());
            deal.setAgreementNumber(dealDto.getAgreementNumber());
            deal.setAgreementDate(dealDto.getAgreementDate());
            deal.setAgreementStartDt(dealDto.getAgreementStartDt());
            deal.setAvailabilityDate(dealDto.getAvailabilityDate());
            deal.setCloseDt(dealDto.getCloseDt());
            deal.setModifyDate(LocalDateTime.now());
        } else {
            deal = DealMapper.toEntity(dealDto);
            deal.setStatus(dealStatusDraft);
        }

        deal.setType(dealType);
        Deal savedDeal = dealRepository.save(deal);

        if (dealDto.getSum() != null) {
            DealSumDto sumDto = dealDto.getSum();
            Currency currency = currencyRepository.findByIdAndIsActiveTrue(sumDto.getCurrency())
                    .orElseThrow(() -> new EntityNotFoundException("Currency c id " + sumDto.getCurrency() + " не найдена или неактивна."));

            Optional<DealSum> existingMainSum = dealSumRepository.findByDealIdAndIsMainTrueAndIsActiveTrue(savedDeal.getId());

            DealSum dealSum;
            if (existingMainSum.isPresent()) {
                dealSum = existingMainSum.get();
            } else {
                dealSum = new DealSum();
                dealSum.setDeal(savedDeal);
                dealSum.setIsMain(true);
            }
            dealSum.setSum(sumDto.getValue());
            dealSum.setCurrency(currency);
            dealSum.setIsActive(true);

            dealSumRepository.save(dealSum);
        }

        return DealMapper.mapToDto(savedDeal);
    }

    @Override
    @Transactional
    public DealDto changeDealStatus(UUID dealId, DealStatusUpdateRequest request) {
        Deal deal = dealRepository.findByIdAndIsActiveTrue(dealId)
                .orElseThrow(() -> new EntityNotFoundException("Deal с id " + dealId + " не найдена или неактивна."));

        DealStatus newStatus = dealStatusRepository.findByIdAndIsActiveTrue(request.getNewStatusId())
                .orElseThrow(() -> new EntityNotFoundException("Новый DealStatus с id " + request.getNewStatusId() + " не найден или неактивен."));

        deal.setStatus(newStatus);
        deal.setModifyDate(LocalDateTime.now());
        if (newStatus.getId().equals("CLOSED")) {
            deal.setCloseDt(LocalDateTime.now());
        }

        Deal updatedDeal = dealRepository.save(deal);
        return DealMapper.mapToDto(updatedDeal);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DealDto> searchDeals(DealSearchRequest request, Pageable pageable) {
        Specification<Deal> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("isActive")));

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getDescription() != null && !request.getDescription().isEmpty()) {
                predicates.add(cb.equal(root.get("description"), request.getDescription()));
            }

            if (request.getAgreementNumber() != null && !request.getAgreementNumber().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("agreementNumber")), "%" + request.getAgreementNumber().toLowerCase() + "%"));
            }

            if (request.getAgreementDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("agreementDate"), request.getAgreementDateFrom()));
            }
            if (request.getAgreementDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("agreementDate"), request.getAgreementDateTo()));
            }

            if (request.getAvailabilityDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("availabilityDate"), request.getAvailabilityDateFrom()));
            }
            if (request.getAvailabilityDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("availabilityDate"), request.getAvailabilityDateTo()));
            }

            if (request.getTypeIds() != null && !request.getTypeIds().isEmpty()) {
                predicates.add(root.get("type").get("id").in(request.getTypeIds()));
            }

            if (request.getStatusIds() != null && !request.getStatusIds().isEmpty()) {
                predicates.add(root.get("status").get("id").in(request.getStatusIds()));
            }

            if (request.getCloseDtFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("closeDt"), request.getCloseDtFrom()));
            }
            if (request.getCloseDtTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("closeDt"), request.getCloseDtTo()));
            }

            // Фильтр по заемщикам (borrower_search)
            if (request.getBorrowerSearch() != null && !request.getBorrowerSearch().isEmpty()) {
                predicates.add(cb.or(
                        cb.like(cb.lower(root.join("dealContractors").get("contractorId")), "%" + request.getBorrowerSearch().toLowerCase() + "%"),
                        cb.like(cb.lower(root.join("dealContractors").get("name")), "%" + request.getBorrowerSearch().toLowerCase() + "%"),
                        cb.like(cb.lower(root.join("dealContractors").get("inn")), "%" + request.getBorrowerSearch().toLowerCase() + "%")
                ));
            }

            // Фильтр по поручителям (warranty_search)
            if (request.getWarrantySearch() != null && !request.getWarrantySearch().isEmpty()) {
                // query.distinct(true);
                predicates.add(cb.or(
                        cb.like(cb.lower(root.join("dealContractors").get("contractorId")), "%" + request.getWarrantySearch().toLowerCase() + "%"),
                        cb.like(cb.lower(root.join("dealContractors").get("name")), "%" + request.getWarrantySearch().toLowerCase() + "%"),
                        cb.like(cb.lower(root.join("dealContractors").get("inn")), "%" + request.getWarrantySearch().toLowerCase() + "%")
                ));
            }

            // Фильтр по сумме (sum)
            if (request.getSumValue() != null || request.getSumCurrency() != null) {

                jakarta.persistence.criteria.Join<Deal, DealSum> dealSumsJoin = root.join("dealSums");
                predicates.add(cb.isTrue(dealSumsJoin.get("isMain")));
                predicates.add(cb.isTrue(dealSumsJoin.get("isActive")));

                if (request.getSumValue() != null) {
                    predicates.add(cb.equal(dealSumsJoin.get("sum"), request.getSumValue()));
                }
                if (request.getSumCurrency() != null && !request.getSumCurrency().isEmpty()) {
                    predicates.add(cb.equal(dealSumsJoin.get("currency").get("id"), request.getSumCurrency()));
                }
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Deal> dealPage = dealRepository.findAll(spec, pageable);

        return dealPage.map(DealMapper::mapToDto);
    }

}
