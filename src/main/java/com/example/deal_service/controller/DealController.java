package com.example.deal_service.controller;

import com.example.deal_service.model.DealRequest;
import com.example.deal_service.model.DealSearchRequest;
import com.example.deal_service.model.DealStatusUpdateRequest;
import com.example.deal_service.model.dto.DealDto;
import com.example.deal_service.service.DealService;
import com.example.deal_service.model.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Контроллер для управления сделками.
 */
@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
@Tag(name = "Управление сделками", description = "API для операций со сделками, поиска и экспорта")
public class DealController {

    private final DealService dealService;

    /**
     * Получает сделку по её уникальному идентификатору.
     *
     * @param id ID сделки.
     * @return DTO с данными сделки.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получить сделку по ID", description = "Возвращает подробную информацию о сделке по её UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сделка найдена",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DealDto.class))}),
            @ApiResponse(responseCode = "404", description = "Сделка не найдена", content = @Content)
    })
    public ResponseEntity<DealDto> getDealById(@PathVariable UUID id) {
        DealDto deal = dealService.getDealById(id);
        return ResponseEntity.ok(deal); // Возвращаем 200 OK с DTO сделки
    }

    /**
     * Сохраняет (создает или обновляет) сделку.
     *
     * @param request Запрос с данными для сохранения сделки.
     * @return DTO сохраненной сделки.
     */
    @PostMapping("/save")
    @Operation(summary = "Сохранить сделку", description = "Создает новую сделку или обновляет существующую на основе переданных данных. " +
            "Указывать поле \"id\" в JSON только в том случае, если не собираетесь создавать новую сделку, а хотите изменить существующую.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сделка успешно сохранена",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DealDto.class))}),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
            @ApiResponse(responseCode = "404", description = "Сделка, тип сделки или статус сделки не найдены", content = @Content)
    })
    public ResponseEntity<DealDto> saveDeal(@RequestBody DealRequest request) {
        DealDto savedDeal = dealService.saveDeal(request);
        return ResponseEntity.ok(savedDeal);
    }

    /**
     * Изменяет статус сделки.
     *
     * @param request Запрос на обновление статуса, содержащий ID сделки и ID нового статуса.
     * @return DTO обновленной сделки с новым статусом.
     */
    @PatchMapping("/change/status")
    @Operation(summary = "Изменить статус сделки", description = "Обновляет статус для указанной сделки.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус успешно изменен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DealDto.class))}),
            @ApiResponse(responseCode = "404", description = "Сделка или статус не найдены", content = @Content)
    })
    public ResponseEntity<DealDto> changeDealStatus(@RequestBody DealStatusUpdateRequest request) {
        DealDto updatedDeal = dealService.changeDealStatus(request.getDealId(), request);
        return ResponseEntity.ok(updatedDeal);
    }

    /**
     * Выполняет поиск сделок по заданным критериям с пагинацией.
     *
     * @param request  Объект с критериями поиска.
     * @param pageable Параметры пагинации (page, size, sort).
     * @return Страница с найденными сделками.
     */
    @PostMapping("/search")
    @Operation(summary = "Поиск сделок", description = "Ищет сделки по различным критериям с поддержкой пагинации и сортировки.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Поиск успешно выполнен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<DealDto>> searchDeals(
            @RequestBody DealSearchRequest request,
            Pageable pageable) {
        Page<DealDto> resultPage = dealService.searchDeals(request, pageable);
        return ResponseEntity.ok(resultPage);
    }

    /**
     * Экспортирует результаты поиска сделок в файл формата XLSX.
     *
     * @param searchRequest Критерии поиска для экспорта.
     * @param page          Номер страницы для экспорта.
     * @param size          Размер страницы для экспорта.
     * @return Файл Excel в виде массива байт.
     */
    @PostMapping("/search/export")
    @Operation(summary = "Экспорт сделок в Excel", description = "Формирует и возвращает XLSX файл с данными сделок по заданным критериям поиска.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл Excel успешно сформирован",
                    content = @Content(mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры поиска", content = @Content)
    })
    public ResponseEntity<byte[]> exportDeals(@RequestBody DealSearchRequest searchRequest,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        byte[] excelBytes = dealService.exportDealsToExcel(searchRequest, new Pagination(page, size));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "deals_export.xlsx");
        headers.setContentLength(excelBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

}
