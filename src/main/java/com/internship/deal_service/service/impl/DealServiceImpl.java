package com.internship.deal_service.service.impl;

import com.internship.deal_service.exception.DealException;
import com.internship.deal_service.model.DealRequest;
import com.internship.deal_service.model.Deal;
import com.internship.deal_service.model.DealType;
import com.internship.deal_service.model.DealStatus;
import com.internship.deal_service.model.dto.DealSumRequest;
import com.internship.deal_service.model.Currency;
import com.internship.deal_service.model.DealSum;
import com.internship.deal_service.model.dto.DealStatusUpdateRequest;
import com.internship.deal_service.model.dto.DealSearchRequest;
import com.internship.deal_service.model.DealContractor;
import com.internship.deal_service.model.ContractorToRole;
import com.internship.deal_service.model.dto.DealDto;
import com.internship.deal_service.model.mapper.DealMapper;
import com.internship.deal_service.model.mapper.DealSumMapper;
import com.internship.deal_service.repository.DealRepository;
import com.internship.deal_service.repository.DealTypeRepository;
import com.internship.deal_service.repository.DealStatusRepository;
import com.internship.deal_service.repository.DealSumRepository;
import com.internship.deal_service.repository.CurrencyRepository;

import com.internship.deal_service.service.DealService;
import com.internship.deal_service.service.file.DealXlsxGenerator;
import com.internship.deal_service.model.Pagination;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.hibernate.LazyInitializationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Реализация основного сервиса {@link DealService} для управления сделками.
 * <p>
 * Этот класс инкапсулирует всю бизнес-логику, связанную с созданием,
 * поиском, обновлением, изменением статуса и экспортом сделок.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final DealTypeRepository dealTypeRepository;
    private final DealStatusRepository dealStatusRepository;
    private final DealSumRepository dealSumRepository;
    private final CurrencyRepository currencyRepository;

    /**
     * {@inheritDoc}
     * Поиск сущности {@link Deal} в БД по передаваемому ID
     * @throws EntityNotFoundException если сделка с указанным ID не найдена или неактивна.
     */
    @Override
    @Transactional(readOnly = true)
    public DealDto getDealById(UUID id) {
        Deal deal = dealRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Deal с id " + id + " не найдена или неактивна"));
        return DealMapper.mapToDto(deal);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Если в запросе {@code request} передан ID, метод обновляет существующую сделку.
     * В противном случае создается новая сделка со статусом "DRAFT".
     * Метод также обрабатывает связанные суммы ({@link DealSum}), гарантируя,
     * что только одна из них может быть основной ({@code isMain = true}).
     * </p>
     * @throws EntityNotFoundException если связанные сущности (тип, статус, валюта) не найдены,
     * или при попытке обновления не найдена сама сделка.
     */
    @Override
    @Transactional
    public DealDto saveDeal(DealRequest request) {
        DealType dealType = dealTypeRepository.findByIdAndIsActiveTrue(request.getType().getId())
                .orElseThrow(() -> new EntityNotFoundException("DealType с id " + request.getType().getId() + " не был найден или неактивен."));

        DealStatus dealStatusDraft = dealStatusRepository.findByIdAndIsActiveTrue("DRAFT")
                .orElseThrow(() -> new EntityNotFoundException("DealStatus \"DRAFT\" не был найден или неактивен."));

        Deal deal;
        // Если передаётся id из DealRequest, то обновляем существующего
        if (request.getId() != null) {
            deal = dealRepository.findByIdAndIsActiveTrue(request.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Deal с id " + request.getId() + " не найдена или неактивна для обновления."));
            deal.setDescription(request.getDescription());
            deal.setAgreementNumber(request.getAgreementNumber());
            deal.setAgreementDate(request.getAgreementDate());
            deal.setAgreementStartDt(request.getAgreementStartDt());
            deal.setAvailabilityDate(request.getAvailabilityDate());
            deal.setCloseDt(request.getCloseDt());
            deal.setModifyDate(LocalDateTime.now());
        } else {
            // иначе создаём нового
            deal = DealMapper.dealRequestToEntity(request);
            deal.setStatus(dealStatusDraft);
        }

        deal.setType(dealType);
        Deal savedDeal = dealRepository.save(deal);
        DealDto savedDealDto = DealMapper.mapToDto(savedDeal);
        if (request.getSum() != null) {

            List<DealSumRequest> sumsRequest = request.getSum();
            sumsRequest.forEach(sumRequest -> {
                Currency currency = currencyRepository.findByIdAndIsActiveTrue(sumRequest.getCurrency())
                        .orElseThrow(() -> new EntityNotFoundException("Currency с id " + sumRequest.getCurrency() + " не найдена или неактивна."));

                DealSum sum = DealSum.builder()
                        .deal(savedDeal)
                        .sum(sumRequest.getValue())
                        .currency(currency)
                        .isMain(sumRequest.getIsMain())
                        .build();

                DealSum savedDealSum = dealSumRepository.save(sum);
                if (sum.getIsMain()) {
                    savedDealDto.setSum(DealSumMapper.toDto(savedDealSum));
                    Set<DealSum> dealSums = savedDeal.getDealSums();
                    dealSums.forEach(ds -> {
                        if (!ds.getId().equals(savedDealSum.getId())) {
                            ds.setIsMain(Boolean.FALSE);
                        }
                    });
                }

            });
        }

        return savedDealDto;
    }

    /**
     * {@inheritDoc}
     * Метод для изменения статуса сделки
     * <p>
     * При изменении статуса на "CLOSED", метод также автоматически устанавливает
     * дату закрытия сделки ({@code closeDt}) на текущее время.
     * </p>
     * @throws DealException если сделка или новый статус не найдены или неактивны.
     */
    @Override
    @Transactional
    public DealDto changeDealStatus(UUID dealId, DealStatusUpdateRequest request) {
        Deal deal = dealRepository.findByIdAndIsActiveTrue(dealId)
                .orElseThrow(() -> new DealException("Deal с id <<" + dealId + ">> не найдена или неактивна."));

        DealStatus newStatus = dealStatusRepository.findByIdAndIsActiveTrue(request.getNewStatusId())
                .orElseThrow(() -> new DealException("Новый DealStatus с id <<" + request.getNewStatusId() + ">> не найден или неактивен."));

        deal.setStatus(newStatus);
        deal.setModifyDate(LocalDateTime.now());
        if (newStatus.getId().equals("CLOSED")) {
            deal.setCloseDt(LocalDateTime.now());
        }

        Deal updatedDeal = dealRepository.save(deal);
        return DealMapper.mapToDto(updatedDeal);
    }

    /**
     * {@inheritDoc}
     * Поиск сделки по фильтрации.
     * <p>
     * Использует {@link Specification} для построения динамического запроса
     * на основе предоставленных фильтров.
     * </p>
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DealDto> searchDeals(DealSearchRequest request, Pagination pagination) {
        Page<Deal> dealPage = dealRepository.findAll(searchFilters(request), PageRequest.of(pagination.getPage(), pagination.getSize()));
        return dealPage.map(DealMapper::mapToDto);
    }

    /**
     * {@inheritDoc}
     * Поиск сделки по фильтрации для создания XLSX файла.
     */
    @Override
    @Transactional(readOnly = true)
    public byte[] exportDealsToExcel(DealSearchRequest searchRequest, Pagination pagination) {
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), Sort.unsorted());
        Specification<Deal> spec = searchFiltersForExport(searchRequest);

        Page<Deal> dealsPage = dealRepository.findAll(spec, pageable);
        List<Deal> deals = dealsPage.getContent();

        return DealXlsxGenerator.createAndFillDealXlsxTable(deals);
    }

    /**
     * Приватный метод-помощник для построения объекта {@link Specification} для поиска сделок.
     * <p>
     * Трансформирует поля из {@link DealSearchRequest} в набор предикатов JPA Criteria API.
     * </p>
     * @param request Объект с критериями поиска.
     * @return {@link Specification} для использования в запросе репозитория.
     */
    private Specification<Deal> searchFilters(DealSearchRequest request) {
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
        return spec;
    }

    /**
     * Приватный метод-помощник для построения объекта {@link Specification} для экспорта сделок.
     * Может содержать логику, отличающуюся от обычного поиска.
     *
     * @param searchRequest Объект с критериями поиска.
     * @return {@link Specification} для использования в запросе репозитория.
     */
    private Specification<Deal> searchFiltersForExport(DealSearchRequest searchRequest) {

        Specification<Deal> spec = (root, query, cb) -> cb.isTrue(root.get("isActive"));

        if (searchRequest.getId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("id"), searchRequest.getId()));
        }
        if (searchRequest.getDescription() != null && !searchRequest.getDescription().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("description"), "%" + searchRequest.getDescription() + "%"));
        }
        if (searchRequest.getAgreementNumber() != null && !searchRequest.getAgreementNumber().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("agreementNumber"), "%" + searchRequest.getAgreementNumber() + "%"));
        }
        if (searchRequest.getAgreementDateFrom() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("agreementDate"), searchRequest.getAgreementDateFrom()));
        }
        if (searchRequest.getAgreementDateTo() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("agreementDate"), searchRequest.getAgreementDateTo()));
        }
        if (searchRequest.getAvailabilityDateFrom() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("availabilityDate"), searchRequest.getAvailabilityDateFrom()));
        }
        if (searchRequest.getAvailabilityDateTo() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("availabilityDate"), searchRequest.getAvailabilityDateTo()));
        }
        if (searchRequest.getTypeIds() != null && !searchRequest.getTypeIds().isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("type").get("id").in(searchRequest.getTypeIds()));
        }
        if (searchRequest.getStatusIds() != null && !searchRequest.getStatusIds().isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("status").get("id").in(searchRequest.getStatusIds()));
        }
        if (searchRequest.getCloseDtFrom() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("closeDt"), searchRequest.getCloseDtFrom()));
        }
        if (searchRequest.getCloseDtTo() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("closeDt"), searchRequest.getCloseDtTo()));
        }
        if (searchRequest.getBorrowerSearch() != null && !searchRequest.getBorrowerSearch().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Deal, DealContractor> contractors = root.join("dealContractors", JoinType.INNER);
                Join<DealContractor, ContractorToRole> contractorRoles = contractors.join("roles", JoinType.INNER);
                return cb.and(
                        cb.like(contractors.get("name"), "%" + searchRequest.getBorrowerSearch() + "%"),
                        cb.equal(contractorRoles.get("role").get("category"), "BORROWER"),
                        cb.isTrue(contractorRoles.get("isActive"))
                );
            });
        }
        if (searchRequest.getWarrantySearch() != null && !searchRequest.getWarrantySearch().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Deal, DealContractor> contractors = root.join("dealContractors", JoinType.INNER);
                Join<DealContractor, ContractorToRole> contractorRoles = contractors.join("roles", JoinType.INNER);
                return cb.and(
                        cb.like(contractors.get("name"), "%" + searchRequest.getWarrantySearch() + "%"),
                        cb.equal(contractorRoles.get("role").get("category"), "WARRANTY"),
                        cb.isTrue(contractorRoles.get("isActive"))
                );
            });
        }
        if (searchRequest.getSumValue() != null) {
            spec = spec.and((root, query, cb) -> {
                Join<Deal, DealSum> dealSums = root.join("dealSums", JoinType.INNER);
                return cb.equal(dealSums.get("value"), searchRequest.getSumValue());
            });
        }
        if (searchRequest.getSumCurrency() != null && !searchRequest.getSumCurrency().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Deal, DealSum> dealSums = root.join("dealSums", JoinType.INNER);
                return cb.equal(dealSums.get("currency").get("id"), searchRequest.getSumCurrency());
            });
        }
        return spec;
    }

}
