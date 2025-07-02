package com.example.deal_service.controller;

import com.example.deal_service.model.ContractorRoleRequest;
import com.example.deal_service.model.dto.ContractorRoleDto;
import com.example.deal_service.service.ContractorRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления ролями контрагентов в сделках.
 * Позволяет добавлять и удалять роли для указанного контрагента.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/contractor-to-role")
@Tag(name = "Управление ролями контрагентов",
    description = "API для добавления и удаления ролей у контрагентов в рамках сделки")
public class ContractorRoleController {

    private final ContractorRoleService contractorRoleService;

    /**
     * Добавляет роль контрагенту в сделке.
     *
     * @param request Запрос, содержащий ID контрагента сделки и ID роли.
     * @return DTO с информацией о добавленной роли.
     */
    @PostMapping("/add")
    @Operation(summary = "Добавить роль контрагенту", description = "Привязывает указанную роль к контрагенту в сделке.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роль успешно добавлена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContractorRoleDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content),
            @ApiResponse(responseCode = "404", description = "Контрагент или роль не найдены", content = @Content)
    })
    public ResponseEntity<ContractorRoleDto> addRoleToContractor(@RequestBody ContractorRoleRequest request) {
        ContractorRoleDto addedRole = contractorRoleService.addRoleToContractor(request);
        return ResponseEntity.ok(addedRole);
    }

    /**
     *  Логически удаляет роль у контрагента в сделке.
     *
     * @param request Запрос, содержащий ID контрагента сделки и ID роли для удаления.
     * @return Ответ без содержимого (204 No Content).
     */
    @DeleteMapping("/delete")
    @Operation(summary = "Удалить роль у контрагента", description = "Отвязывает указанную роль от контрагента в сделке.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Роль успешно удалена", content = @Content),
            @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content),
            @ApiResponse(responseCode = "404", description = "Связь контрагента и роли не найдена", content = @Content)
    })
    public ResponseEntity<Void> deleteRoleFromContractor(@RequestBody ContractorRoleRequest request) {
        contractorRoleService.deleteRoleFromContractor(request);
        return ResponseEntity.noContent().build();
    }

}
