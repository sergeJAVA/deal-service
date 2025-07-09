package com.internship.deal_service.service.file;

import com.internship.deal_service.model.Deal;
import com.internship.deal_service.model.DealContractor;
import com.internship.deal_service.model.DealSum;
import com.internship.deal_service.service.DealFillerService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилитный класс для генерации Excel (XLSX) файла со списком сделок.
 */
public final class DealXlsxGenerator {

    private DealXlsxGenerator() {

    }

    /**
     * Создает и заполняет Excel-таблицу на основе списка сделок.
     *
     * @param deals Список сущностей {@link Deal} для экспорта.
     * @return Массив байт, представляющий сгенерированный XLSX файл.
     * @throws RuntimeException в случае ошибки ввода-вывода при создании файла.
     */
    public static byte[] createAndFillDealXlsxTable(List<Deal> deals) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Deals");

            String[] headers = DealFillerService.HEADERS;

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle(workbook));
            }

            int rowNum = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");

            for (Deal deal : deals) {
                List<DealSum> sums = new ArrayList<>(deal.getDealSums());
                List<DealContractor> contractors = new ArrayList<>(deal.getDealContractors());

                boolean hasSums = !sums.isEmpty();
                boolean hasContractors = !contractors.isEmpty();

                // Блок 1: Первая строка сущности Deal
                Row dealBaseRow = sheet.createRow(rowNum++);
                DealFillerService.fillDealBaseColumns(dealBaseRow, deal, dateFormatter, greenCellStyle(workbook));
                DealFillerService.fillEmptySumColumns(dealBaseRow, greenCellStyle(workbook));
                DealFillerService.fillEmptyContractorColumns(dealBaseRow, greenCellStyle(workbook));

                // Блок 2: Суммы
                if (hasSums) {
                    for (int i = 0; i < sums.size(); i++) {
                        Row row = sheet.createRow(rowNum++);
                        DealFillerService.fillEmptyBaseColumns(row, defaultStyle(workbook));
                        DealFillerService.fillDealSumColumns(row, sums.get(i), orangeStyle(workbook));
                        DealFillerService.fillEmptyContractorColumns(row, orangeStyle(workbook));
                    }
                }

                // Блок 3: Контрагенты
                // Этот блок должен идти после всех сумм, если они есть
                if (hasContractors) {
                    for (int i = 0; i < contractors.size(); i++) {
                        Row row = sheet.createRow(rowNum++);
                        DealFillerService.fillEmptyBaseColumns(row, defaultStyle(workbook));
                        DealFillerService.fillEmptySumColumns(row, defaultStyle(workbook));
                        DealFillerService.fillDealContractorColumns(row, contractors.get(i), blueCellStyle(workbook));
                    }
                }

            }

            // Авторазмер колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            int descriptionColumnIndex = 1;
            sheet.setColumnWidth(descriptionColumnIndex, 256 * 20);

            int statusColumnIndex = 7;
            sheet.setColumnWidth(statusColumnIndex, 256 * 20);

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при генерации Excel файла", e);
        }
    }

    private static CellStyle greenCellStyle(Workbook workbook) {
        CellStyle greenCellStyle = workbook.createCellStyle();
        XSSFColor customGreenColor = new XSSFColor(new byte[]{(byte) 198, (byte) 239, (byte) 206}, null);
        greenCellStyle.setFillForegroundColor(customGreenColor);
        greenCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        greenCellStyle.setBorderBottom(BorderStyle.THIN);
        greenCellStyle.setBorderTop(BorderStyle.THIN);
        greenCellStyle.setBorderLeft(BorderStyle.THIN);
        greenCellStyle.setBorderRight(BorderStyle.THIN);

        return greenCellStyle;
    }

    private static CellStyle blueCellStyle(Workbook workbook) {
        CellStyle blueCellStyle = workbook.createCellStyle();
        XSSFColor customBlueColor = new XSSFColor(new byte[]{(byte) 217, (byte) 228, (byte) 240}, null);
        blueCellStyle.setFillForegroundColor(customBlueColor);
        blueCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        blueCellStyle.setBorderBottom(BorderStyle.THIN);
        blueCellStyle.setBorderTop(BorderStyle.THIN);
        blueCellStyle.setBorderLeft(BorderStyle.THIN);
        blueCellStyle.setBorderRight(BorderStyle.THIN);

        return blueCellStyle;
    }

    private static CellStyle headerStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        return headerStyle;
    }

    private static CellStyle defaultStyle(Workbook workbook) {
        CellStyle defaultCellStyle = workbook.createCellStyle();
        defaultCellStyle.setBorderBottom(BorderStyle.THIN);
        defaultCellStyle.setBorderTop(BorderStyle.THIN);
        defaultCellStyle.setBorderLeft(BorderStyle.THIN);
        defaultCellStyle.setBorderRight(BorderStyle.THIN);

        return defaultCellStyle;
    }

    private static CellStyle orangeStyle(Workbook workbook) {
        CellStyle orangeCellStyle = workbook.createCellStyle();
        XSSFColor customOrangeColor = new XSSFColor(new byte[]{(byte) 255, (byte) 235, (byte) 204}, null);
        orangeCellStyle.setFillForegroundColor(customOrangeColor);
        orangeCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        orangeCellStyle.setBorderBottom(BorderStyle.THIN);
        orangeCellStyle.setBorderTop(BorderStyle.THIN);
        orangeCellStyle.setBorderLeft(BorderStyle.THIN);
        orangeCellStyle.setBorderRight(BorderStyle.THIN);

        return orangeCellStyle;
    }

}
