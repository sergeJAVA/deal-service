package com.example.deal_service.service;

import com.example.deal_service.model.*;
import com.example.deal_service.model.dto.DealDto;
import com.example.deal_service.model.mapper.DealMapper;
import com.example.deal_service.model.mapper.DealSumMapper;
import com.example.deal_service.repository.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
            sumsRequest.forEach( sumRequest -> {
                Currency currency = currencyRepository.findByIdAndIsActiveTrue(sumRequest.getCurrency())
                        .orElseThrow(() -> new EntityNotFoundException("Currency с id " + sumRequest.getCurrency() + " не найдена или неактивна."));

                DealSum sum = new DealSum();

                sum.setDeal(savedDeal);
                sum.setSum(sumRequest.getValue());
                sum.setCurrency(currency);
                sum.setIsMain(sumRequest.getIsMain());

                DealSum savedDealSum = dealSumRepository.save(sum);
                if (sum.getIsMain()) {
                    savedDealDto.setSum(DealSumMapper.toDto(savedDealSum));
                    List<DealSum> dealSums = savedDeal.getDealSums();
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

    @Override
    @Transactional(readOnly = true)
    public byte[] exportDealsToExcel(DealSearchRequest searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), Sort.unsorted());
        Specification<Deal> spec = (root, query, cb) -> cb.isTrue(root.get("isActive"));

        // ... (твоя логика фильтрации - без изменений) ...
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

        Page<Deal> dealsPage = dealRepository.findAll(spec, pageable);
        List<Deal> deals = dealsPage.getContent();

        for (Deal deal : deals) {
            Hibernate.initialize(deal.getDealContractors()); // Инициализация коллекции dealContractors
            if (deal.getDealContractors() != null) {
                for (DealContractor contractor : deal.getDealContractors()) {
                    Hibernate.initialize(contractor.getRoles()); // Инициализация коллекции ролей контрагента
                    if (contractor.getRoles() != null) {
                        for (ContractorToRole roleLink : contractor.getRoles()) {
                            Hibernate.initialize(roleLink.getRole()); // Инициализация самой роли
                        }
                    }
                }
            }
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()){
            Sheet sheet = workbook.createSheet("Deals");

            String[] headers = {
                    "ИД сделки", "Описание", "Номер договора", "Дата договора", "Дата и время вступления соглашения в силу",
            "Срок действия сделки", "Тип сделки", "Статус сделки", "Сумма сделки", "Наименование валюты", "Основная сумма сделки",
            "Наименование контрагента", "ИНН контрагента", "Роли контрагента"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle headerStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");

            for (Deal deal : deals) {
                List<DealSum> sums = new ArrayList<>(deal.getDealSums());
                List<DealContractor> contractors = new ArrayList<>(deal.getDealContractors());

                boolean hasSums = !sums.isEmpty();
                boolean hasContractors = !contractors.isEmpty();

                // Флаг, чтобы базовая информация о сделке выводилась только один раз
                boolean dealBaseInfoWritten = false;

                // Блок 1: Вывод сделки + Суммы
                if (hasSums) {
                    for (int i = 0; i < sums.size(); i++) {
                        Row row = sheet.createRow(rowNum++);
                        // Заполняем базовую информацию о сделке только для первой строки (будь то первая сумма или первая строка без сумм, но с контрагентами)
                        if (!dealBaseInfoWritten) {
                            fillDealBaseColumns(row, deal, dateFormatter);
                            dealBaseInfoWritten = true;
                        } else {
                            fillEmptyBaseColumns(row); // Остальные строки сделки - пустые базовые поля
                        }

                        // Заполняем информацию о текущей сумме
                        fillDealSumColumns(row, sums.get(i));

                        // Ячейки контрагентов здесь всегда пустые
                        fillEmptyContractorColumns(row);
                    }
                }

                // Блок 2: Контрагенты
                // Этот блок должен идти после всех сумм, если они есть
                if (hasContractors) {
                    for (int i = 0; i < contractors.size(); i++) {
                        Row row = sheet.createRow(rowNum++);
                        // Заполняем базовую информацию о сделке только для первой строки (если она еще не была записана)
                        if (!dealBaseInfoWritten) {
                            fillDealBaseColumns(row, deal, dateFormatter);
                            dealBaseInfoWritten = true;
                        } else {
                            fillEmptyBaseColumns(row); // Остальные строки сделки - пустые базовые поля
                        }

                        // Ячейки сумм здесь всегда пустые
                        fillEmptySumColumns(row);

                        // Заполняем информацию о текущем контрагенте
                        fillDealContractorColumns(row, contractors.get(i));
                    }
                }

                // Блок 3: Если нет ни сумм, ни контрагентов
                // Этот случай будет обработан, если dealBaseInfoWritten так и остался false (т.е. ни один из вышеуказанных блоков не выполнился)
                if (!dealBaseInfoWritten) {
                    Row row = sheet.createRow(rowNum++);
                    fillDealBaseColumns(row, deal, dateFormatter);
                    fillEmptySumColumns(row);
                    fillEmptyContractorColumns(row);
                }
            }

            // Авторазмер колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при генерации Excel файла", e);
        }
    }

    // Вспомогательные методы (без изменений)
    private void fillDealBaseColumns(Row row, Deal deal, DateTimeFormatter dateFormatter) {
        row.createCell(0).setCellValue(deal.getId().toString());
        row.createCell(1).setCellValue(deal.getDescription());
        row.createCell(2).setCellValue(deal.getAgreementNumber());
        row.createCell(3).setCellValue(deal.getAgreementDate() != null ? deal.getAgreementDate().format(dateFormatter) : "");
        row.createCell(4).setCellValue(deal.getAgreementStartDt() != null ? deal.getAgreementStartDt().format(dateFormatter) : "");
        row.createCell(5).setCellValue(deal.getAvailabilityDate() != null ? deal.getAvailabilityDate().format(dateFormatter) : "");
        row.createCell(6).setCellValue(deal.getType() != null ? deal.getType().getName() : "");
        row.createCell(7).setCellValue(deal.getStatus() != null ? deal.getStatus().getName() : "");
    }

    private void fillDealSumColumns(Row row, DealSum dealSum) {
        row.createCell(8).setCellValue(dealSum.getSum() != null ? dealSum.getSum().doubleValue() : 0.0);
        row.createCell(9).setCellValue(dealSum.getCurrency() != null ? dealSum.getCurrency().getName() : "");
        row.createCell(10).setCellValue(dealSum.getIsMain() != null ? (dealSum.getIsMain() ? "Да" : "Нет") : "");
    }

    private void fillDealContractorColumns(Row row, DealContractor contractor) {
        row.createCell(11).setCellValue(contractor.getName());
        row.createCell(12).setCellValue(contractor.getInn());
        String rolesString = "";
        if (contractor.getRoles() != null && !contractor.getRoles().isEmpty()) {
            rolesString = contractor.getRoles().stream()
                    .filter(ContractorToRole::getIsActive)
                    .map(link -> link.getRole() != null ? link.getRole().getName() : "")
                    .filter(name -> !name.isEmpty())
                    .collect(Collectors.joining(", "));
        }
        row.createCell(13).setCellValue(rolesString);
    }

    private void fillEmptyBaseColumns(Row row) {
        for (int i = 0; i <= 7; i++) { // Колонки A (0) до H (7)
            Cell cell = row.createCell(i);
            cell.setCellValue("");
        }
    }

    private void fillEmptySumColumns(Row row) {
        for (int i = 8; i <= 10; i++) { // Колонки I (8) до K (10)
            Cell cell = row.createCell(i);
            cell.setCellValue("");
        }
    }

    private void fillEmptyContractorColumns(Row row) {
        for (int i = 11; i <= 13; i++) { // Колонки L (11) до N (13)
            Cell cell = row.createCell(i);
            cell.setCellValue("");
        }
    }

}
