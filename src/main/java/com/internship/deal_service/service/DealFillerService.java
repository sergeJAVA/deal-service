package com.internship.deal_service.service;

import com.internship.deal_service.model.ContractorToRole;
import com.internship.deal_service.model.Deal;
import com.internship.deal_service.model.DealContractor;
import com.internship.deal_service.model.DealSum;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
    public static void fillDealBaseColumns(Row row, Deal deal, DateTimeFormatter dateFormatter, CellStyle cellStyle) {
        Cell cell = row.createCell(0);
        cell.setCellValue(deal.getId().toString());
        cell.setCellStyle(cellStyle);

        Cell cell1 = row.createCell(1);
        cell1.setCellValue(deal.getDescription());
        cell1.setCellStyle(cellStyle);

        Cell cell2 = row.createCell(2);
        cell2.setCellValue(deal.getAgreementNumber());
        cell2.setCellStyle(cellStyle);

        Cell cell3 = row.createCell(3);
        cell3.setCellValue(deal.getAgreementDate() != null ? deal.getAgreementDate().format(dateFormatter) : "");
        cell3.setCellStyle(cellStyle);

        Cell cell4 = row.createCell(4);
        cell4.setCellValue(deal.getAgreementStartDt() != null ? deal.getAgreementStartDt().format(dateFormatter) : "");
        cell4.setCellStyle(cellStyle);

        Cell cell5 = row.createCell(5);
        cell5.setCellValue(deal.getAvailabilityDate() != null ? deal.getAvailabilityDate().format(dateFormatter) : "");
        cell5.setCellStyle(cellStyle);

        Cell cell6 = row.createCell(6);
        cell6.setCellValue(deal.getType() != null ? deal.getType().getName() : "");
        cell6.setCellStyle(cellStyle);

        Cell cell7 = row.createCell(7);
        cell7.setCellValue(deal.getStatus() != null ? deal.getStatus().getName() : "");
        cell7.setCellStyle(cellStyle);

        for (int i = 8; i < HEADERS.length; i++) { // От колонки I (индекс 8) до конца
            Cell emptyCell = row.createCell(i);
            emptyCell.setCellStyle(cellStyle);
        }
    }

    /**
     * Заполняет ячейки строки информацией о сумме сделки.
     * @param row Строка Excel для заполнения.
     * @param dealSum Сущность суммы сделки.
     */
    public static void fillDealSumColumns(Row row, DealSum dealSum, CellStyle cellStyle) {
        Cell cell8 = row.createCell(8);
        cell8.setCellValue(dealSum.getSum() != null ? dealSum.getSum().doubleValue() : 0.0);
        cell8.setCellStyle(cellStyle);

        Cell cell9 = row.createCell(9);
        cell9.setCellValue(dealSum.getCurrency() != null ? dealSum.getCurrency().getName() : "");
        cell9.setCellStyle(cellStyle);

        Cell cell10 = row.createCell(10);
        cell10.setCellValue(dealSum.getIsMain() != null ? (dealSum.getIsMain() ? "Да" : "Нет") : "");
        cell10.setCellStyle(cellStyle);
    }

    /**
     * Заполняет ячейки строки информацией о контрагенте сделки.
     * @param row Строка Excel для заполнения.
     * @param contractor Сущность контрагента.
     */
    public static void fillDealContractorColumns(Row row, DealContractor contractor, CellStyle cellStyle) {
        Cell cell11 = row.createCell(11);
        cell11.setCellValue(contractor.getName());
        cell11.setCellStyle(cellStyle);

        Cell cell12 = row.createCell(12);
        cell12.setCellValue(contractor.getInn());
        cell12.setCellStyle(cellStyle);

        String rolesString = "";
        if (contractor.getRoles() != null && !contractor.getRoles().isEmpty()) {
            rolesString = contractor.getRoles().stream()
                    .filter(ContractorToRole::getIsActive)
                    .map(link -> link.getRole() != null ? link.getRole().getName() : "")
                    .filter(name -> !name.isEmpty())
                    .collect(Collectors.joining(", "));
        }

        Cell cell13 = row.createCell(13);
        cell13.setCellValue(rolesString);
        cell13.setCellStyle(cellStyle);
    }

    /**
     * Создаёт пустые ячейки от колонки A до H
     * @param row Строка Excel для заполнения.
     */
    public static void fillEmptyBaseColumns(Row row, CellStyle cellStyle) {
        for (int i = 0; i <= 7; i++) { // Колонки A (0) до H (7)
            Cell cell = row.createCell(i);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
        }
    }

    /**
     * Создаёт пустые ячейки от колонки I до K
     * @param row Строка Excel для заполнения.
     */
    public static void fillEmptySumColumns(Row row, CellStyle cellStyle) {
        for (int i = 8; i <= 10; i++) { // Колонки I (8) до K (10)
            Cell cell = row.createCell(i);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
        }
    }

    /**
     * Создаёт пустые ячейки от колонки L до N
     * @param row Строка Excel для заполнения.
     */
    public static void fillEmptyContractorColumns(Row row, CellStyle cellStyle) {
        for (int i = 11; i <= 13; i++) { // Колонки L (11) до N (13)
            Cell cell = row.createCell(i);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
        }
    }

}
