package com.internship.deal_service.controller;

import com.internship.deal_service.model.dto.ContractorRoleDto;
import com.internship.deal_service.model.dto.ContractorRoleRequest;
import com.internship.deal_service.service.ContractorRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ui/contractor-to-role")
@Tag(name = "Управление ролями контрагентов c аутентификацией",
    description = "API для назначения и удаления ролей контрагентам, которое требует аутентификации")
public class UIContractorRoleController {

    private final ContractorRoleService contractorRoleService;

    @Operation(summary = "Добавить роль контрагенту",
            description = "Назначает указанную роль контрагенту. Требует роли SUPERUSER или DEAL_SUPERUSER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роль успешно добавлена"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)"),
            @ApiResponse(responseCode = "404", description = "Контрагент или роль не найдены")
    })
    @PostMapping("/add")
    public ResponseEntity<ContractorRoleDto> addRoleToContractor(@RequestBody ContractorRoleRequest request) {
        ContractorRoleDto addedRole = contractorRoleService.addRoleToContractor(request);
        return ResponseEntity.ok(addedRole);
    }

    @Operation(summary = "Удалить роль у контрагента",
            description = "Удаляет указанную роль у контрагента. Требует роли SUPERUSER или DEAL_SUPERUSER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Роль успешно удалена"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)"),
            @ApiResponse(responseCode = "404", description = "Контрагент или роль не найдены")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteRoleFromContractor(@RequestBody ContractorRoleRequest request) {
        contractorRoleService.deleteRoleFromContractor(request);
        return ResponseEntity.noContent().build();
    }

}
