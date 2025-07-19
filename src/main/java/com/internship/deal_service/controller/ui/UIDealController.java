package com.internship.deal_service.controller.ui;

import com.internship.deal_service.model.DealRequest;
import com.internship.deal_service.model.dto.DealDto;
import com.internship.deal_service.model.dto.DealSearchRequest;
import com.internship.deal_service.model.dto.DealStatusUpdateRequest;
import com.internship.deal_service.model.security.TokenAuthentication;
import com.internship.deal_service.model.security.TokenData;
import com.internship.deal_service.service.DealService;
import com.internship.deal_service.util.UserRoleUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/ui/deal")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Управление сделками c аутентификацией",
        description = "API для создания, получения, поиска и изменения статуса сделок, которое требует аутентификации")
public class UIDealController {

    private final DealService dealService;

    @Operation(summary = "Получить сделку по ID",
            description = "Возвращает детали сделки по ее уникальному идентификатору. Требует роли SUPERUSER или DEAL_SUPERUSER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сделка успешно найдена"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)"),
            @ApiResponse(responseCode = "404", description = "Сделка не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DealDto> getDealById(@PathVariable UUID id) {
        DealDto deal = dealService.getDealById(id);
        return ResponseEntity.ok(deal); // Возвращаем 200 OK с DTO сделки
    }

    @Operation(summary = "Сохранить или обновить сделку",
            description = "Создает новую сделку или обновляет существующую. Требует роли SUPERUSER или DEAL_SUPERUSER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сделка успешно сохранена/обновлена"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'DEAL_SUPERUSER')")
    public ResponseEntity<DealDto> saveDeal(@RequestBody DealRequest request,
                                            Authentication authentication) {

        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        TokenData tokenData = tokenAuthentication.getTokenData();

        DealDto savedDeal = dealService.saveDealWithUserId(request, tokenData.getId().toString());
        return ResponseEntity.ok(savedDeal);
    }

    @Operation(summary = "Изменить статус сделки",
            description = "Изменяет статус существующей сделки. Требует роли SUPERUSER или DEAL_SUPERUSER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус сделки успешно изменен"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)"),
            @ApiResponse(responseCode = "404", description = "Сделка не найдена")
    })
    @PatchMapping("/change/status")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'DEAL_SUPERUSER')")
    public ResponseEntity<DealDto> changeDealStatus(@RequestBody DealStatusUpdateRequest request) {
        DealDto updatedDeal = dealService.changeDealStatus(request.getDealId(), request);
        return ResponseEntity.ok(updatedDeal);
    }

    @Operation(summary = "Поиск сделок",
            description = "Выполняет поиск сделок с учетом фильтров и пагинации. Доступ зависит от ролей пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список сделок успешно получен"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'DEAL_SUPERUSER', 'CREDIT_USER', 'OVERDRAFT_USER')")
    public ResponseEntity<Page<DealDto>> searchDeals(
            @RequestBody DealSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        Page<DealDto> resultPage = UserRoleUtil.searchDealConditionals(tokenAuthentication, dealService, request, page, size);

        return ResponseEntity.ok(resultPage);
    }

    @Operation(summary = "Экспорт сделок в Excel",
            description = "Экспортирует сделки в формате Excel с учетом фильтров и пагинации. Доступ зависит от ролей пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл Excel успешно сгенерирован и отправлен"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    @PostMapping("/search/export")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'DEAL_SUPERUSER', 'CREDIT_USER', 'OVERDRAFT_USER')")
    public ResponseEntity<byte[]> exportDeals(@RequestBody DealSearchRequest searchRequest,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              Authentication authentication) {
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        byte[] excelBytes = UserRoleUtil.exportDealConditionals(tokenAuthentication, searchRequest, dealService, page, size);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "deals_export.xlsx");
        headers.setContentLength(excelBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

}
