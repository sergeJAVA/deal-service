package com.example.deal_service.repository;

import com.example.deal_service.model.ContractorToRole;
import com.example.deal_service.model.ContractorToRoleId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractorToRoleRepository {

    Optional<ContractorToRole> findByIdAndIsActiveTrue(ContractorToRoleId id);

    List<ContractorToRole> findByContractorIdAndIsActiveTrue(UUID contractorId);

    boolean existsByContractorIdAndRoleIdAndIsActiveTrue(UUID contractorId, String roleId);

}
