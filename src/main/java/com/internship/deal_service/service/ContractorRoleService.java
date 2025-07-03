package com.internship.deal_service.service;

import com.internship.deal_service.model.dto.ContractorRoleRequest;
import com.internship.deal_service.model.dto.ContractorRoleDto;

public interface ContractorRoleService {

    ContractorRoleDto addRoleToContractor(ContractorRoleRequest request);

    void deleteRoleFromContractor(ContractorRoleRequest request);

}
