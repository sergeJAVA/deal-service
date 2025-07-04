package com.internship.deal_service.service;

import com.internship.deal_service.model.ContractorToRole;
import com.internship.deal_service.model.Deal;
import com.internship.deal_service.model.DealContractor;
import com.internship.deal_service.model.DealSum;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import com.internship.deal_service.service.file.DealXlsxGenerator;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Сервисный класс-помощник для {@link DealXlsxGenerator}, отвечающий за заполнение
 * ячеек в Excel-файле данными из сущностей.
 */
public final class DealFillerService {

    private DealFillerService() {

    }

    /**
     * Заголовки столбцов для Excel-файла.
     */
    public static final String[] HEADERS = {
            "ИД сделки", "Описание", "Номер договора", "Дата договора", "Дата и время вступления соглашения в силу",
            "Срок действия сделки", "Тип сделки", "Статус сделки", "Сумма сделки", "Наименование валюты", "Основная сумма сделки",
            "Наименование контрагента", "ИНН контрагента", "Роли контрагента"
    };

    /**
     * Заполняет ячейки строки базовой информацией о сделке.
     * @param row Строка Excel для заполнения.
     * @param deal Сущность сделки.
     * @param dateFormatter Форматтер для дат.
     */
    public static void fillDealBaseColumns(Row row, Deal deal, DateTimeFormatter dateFormatter) {
        row.createCell(0).setCellValue(deal.getId().toString());
        row.createCell(1).setCellValue(deal.getDescription());
        row.createCell(2).setCellValue(deal.getAgreementNumber());
        row.createCell(3).setCellValue(deal.getAgreementDate() != null ? deal.getAgreementDate().format(dateFormatter) : "");
        row.createCell(4).setCellValue(deal.getAgreementStartDt() != null ? deal.getAgreementStartDt().format(dateFormatter) : "");
        row.createCell(5).setCellValue(deal.getAvailabilityDate() != null ? deal.getAvailabilityDate().format(dateFormatter) : "");
        row.createCell(6).setCellValue(deal.getType() != null ? deal.getType().getName() : "");
        row.createCell(7).setCellValue(deal.getStatus() != null ? deal.getStatus().getName() : "");
    }

    /**
     * Заполняет ячейки строки информацией о сумме сделки.
     * @param row Строка Excel для заполнения.
     * @param dealSum Сущность суммы сделки.
     */
    public static void fillDealSumColumns(Row row, DealSum dealSum) {
        row.createCell(8).setCellValue(dealSum.getSum() != null ? dealSum.getSum().doubleValue() : 0.0);
        row.createCell(9).setCellValue(dealSum.getCurrency() != null ? dealSum.getCurrency().getName() : "");
        row.createCell(10).setCellValue(dealSum.getIsMain() != null ? (dealSum.getIsMain() ? "Да" : "Нет") : "");
    }

    /**
     * Заполняет ячейки строки информацией о контрагенте сделки.
     * @param row Строка Excel для заполнения.
     * @param contractor Сущность контрагента.
     */
    public static void fillDealContractorColumns(Row row, DealContractor contractor) {
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

    /**
     * Создаёт пустые ячейки от колонки A до H
     * @param row Строка Excel для заполнения.
     */
    public static void fillEmptyBaseColumns(Row row) {
        for (int i = 0; i <= 7; i++) { // Колонки A (0) до H (7)
            Cell cell = row.createCell(i);
            cell.setCellValue("");
        }
    }

    /**
     * Создаёт пустые ячейки от колонки I до K
     * @param row Строка Excel для заполнения.
     */
    public static void fillEmptySumColumns(Row row) {
        for (int i = 8; i <= 10; i++) { // Колонки I (8) до K (10)
            Cell cell = row.createCell(i);
            cell.setCellValue("");
        }
    }

    /**
     * Создаёт пустые ячейки от колонки L до N
     * @param row Строка Excel для заполнения.
     */
    public static void fillEmptyContractorColumns(Row row) {
        for (int i = 11; i <= 13; i++) { // Колонки L (11) до N (13)
            Cell cell = row.createCell(i);
            cell.setCellValue("");
        }
    }

}
