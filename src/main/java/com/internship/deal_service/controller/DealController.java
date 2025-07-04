package com.internship.deal_service.controller;

import com.internship.deal_service.model.DealRequest;
import com.internship.deal_service.model.dto.DealSearchRequest;
import com.internship.deal_service.model.dto.DealStatusUpdateRequest;
import com.internship.deal_service.model.dto.DealDto;
import com.internship.deal_service.service.DealService;
import com.internship.deal_service.model.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @PostMapping("/search")
    @Operation(summary = "Поиск сделок", description = "Ищет сделки по различным критериям с поддержкой пагинации и сортировки.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Поиск успешно выполнен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<DealDto>> searchDeals(
            @RequestBody DealSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DealDto> resultPage = dealService.searchDeals(request, new Pagination(page, size));
        return ResponseEntity.ok(resultPage);
    }

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
