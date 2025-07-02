package com.example.deal_service.service.file;

import com.example.deal_service.model.Deal;
import com.example.deal_service.model.DealContractor;
import com.example.deal_service.model.DealSum;
import com.example.deal_service.service.DealFillerService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DealXlsxGenerator {

    public static byte[] createAndFillDealXlsxTable(List<Deal> deals) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()){
            Sheet sheet = workbook.createSheet("Deals");

            String[] headers = DealFillerService.HEADERS;

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
                            DealFillerService.fillDealBaseColumns(row, deal, dateFormatter);
                            dealBaseInfoWritten = true;
                        } else {
                            DealFillerService.fillEmptyBaseColumns(row); // Остальные строки сделки - пустые базовые поля
                        }

                        // Заполняем информацию о текущей сумме
                        DealFillerService.fillDealSumColumns(row, sums.get(i));

                        // Ячейки контрагентов здесь всегда пустые
                        DealFillerService.fillEmptyContractorColumns(row);
                    }
                }

                // Блок 2: Контрагенты
                // Этот блок должен идти после всех сумм, если они есть
                if (hasContractors) {
                    for (int i = 0; i < contractors.size(); i++) {
                        Row row = sheet.createRow(rowNum++);
                        // Заполняем базовую информацию о сделке только для первой строки (если она еще не была записана)
                        if (!dealBaseInfoWritten) {
                            DealFillerService.fillDealBaseColumns(row, deal, dateFormatter);
                            dealBaseInfoWritten = true;
                        } else {
                            DealFillerService.fillEmptyBaseColumns(row); // Остальные строки сделки - пустые базовые поля
                        }

                        // Ячейки сумм здесь всегда пустые
                        DealFillerService.fillEmptySumColumns(row);

                        // Заполняем информацию о текущем контрагенте
                        DealFillerService.fillDealContractorColumns(row, contractors.get(i));
                    }
                }

                // Блок 3: Если нет ни сумм, ни контрагентов
                // Этот случай будет обработан, если dealBaseInfoWritten так и остался false (т.е. ни один из вышеуказанных блоков не выполнился)
                if (!dealBaseInfoWritten) {
                    Row row = sheet.createRow(rowNum++);
                    DealFillerService.fillDealBaseColumns(row, deal, dateFormatter);
                    DealFillerService.fillEmptySumColumns(row);
                    DealFillerService.fillEmptyContractorColumns(row);
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
}
