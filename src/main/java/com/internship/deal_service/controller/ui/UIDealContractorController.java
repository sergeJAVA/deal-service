package com.internship.deal_service.controller.ui;

import com.internship.deal_service.model.dto.DealContractorDto;
import com.internship.deal_service.model.dto.DealContractorRequest;
import com.internship.deal_service.model.security.TokenAuthentication;
import com.internship.deal_service.model.security.TokenData;
import com.internship.deal_service.service.DealContractorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/ui/deal-contractor")
@RequiredArgsConstructor
@Tag(name = "Управление контрагентами сделки c аутентификацией",
        description = "API для добавления, обновления и удаления контрагентов из сделки, которое требует аутентификации")
public class UIDealContractorController {

    private final DealContractorService dealContractorService;

    @Operation(summary = "Сохранить или обновить контрагента сделки",
            description = "Добавляет нового контрагента к сделке или обновляет существующего. Требует роли SUPERUSER или DEAL_SUPERUSER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Контрагент успешно сохранен/обновлен"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    @PutMapping("/save")
    public ResponseEntity<DealContractorDto> saveDealContractor(@RequestBody DealContractorRequest request,
                                                                Authentication authentication) {

        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        TokenData tokenData = tokenAuthentication.getTokenData();
        DealContractorDto savedDto = dealContractorService.saveDealContractorWithUserId(request, tokenData.getId().toString());
        return ResponseEntity.ok(savedDto);
    }

    @Operation(summary = "Удалить контрагента из сделки",
            description = "Удаляет контрагента по его ID из сделки. Требует роли SUPERUSER или DEAL_SUPERUSER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Контрагент успешно удален"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)"),
            @ApiResponse(responseCode = "404", description = "Контрагент не найден")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteDealContractor(@RequestParam UUID id) {
        dealContractorService.deleteDealContractor(id);
        return ResponseEntity.noContent().build();
    }

}
