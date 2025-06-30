package com.example.deal_service.controller;

import com.example.deal_service.model.ContractorRoleRequest;
import com.example.deal_service.model.dto.ContractorRoleDto;
import com.example.deal_service.service.ContractorRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contractor-to-role")
public class ContractorRoleController {

    private final ContractorRoleService contractorRoleService;

    @PostMapping("/add")
    public ResponseEntity<ContractorRoleDto> addRoleToContractor(@RequestBody ContractorRoleRequest request) {
        ContractorRoleDto addedRole = contractorRoleService.addRoleToContractor(request);
        return ResponseEntity.ok(addedRole);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteRoleFromContractor(@RequestBody ContractorRoleRequest request) {
        contractorRoleService.deleteRoleFromContractor(request);
        return ResponseEntity.noContent().build();
    }

}
