package com.internship.deal_service.controller;

import com.internship.deal_service.model.dto.ContractorRoleDto;
import com.internship.deal_service.model.dto.ContractorRoleRequest;
import com.internship.deal_service.service.ContractorRoleService;
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
public class UIContractorRoleController {

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
