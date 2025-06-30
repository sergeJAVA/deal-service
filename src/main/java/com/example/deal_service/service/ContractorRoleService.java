package com.example.deal_service.service;

import com.example.deal_service.model.ContractorRoleRequest;
import com.example.deal_service.model.dto.ContractorRoleDto;

public interface ContractorRoleService {

    ContractorRoleDto addRoleToContractor(ContractorRoleRequest request);

    void deleteRoleFromContractor(ContractorRoleRequest request);

}
