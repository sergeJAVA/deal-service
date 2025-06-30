package com.example.deal_service.repository;

import com.example.deal_service.model.ContractorToRole;
import com.example.deal_service.model.ContractorToRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractorToRoleRepository extends JpaRepository<ContractorToRole, ContractorToRoleId> {

    @Query("SELECT ctr FROM ContractorToRole ctr WHERE ctr.contractor.id = :contractorId AND ctr.role.id = :roleId")
    Optional<ContractorToRole> findByContractorIdAndRoleId(@Param("contractorId") UUID contractorId, @Param("roleId") String roleId);

    List<ContractorToRole> findByContractorIdAndIsActiveTrue(UUID contractorId);

}
