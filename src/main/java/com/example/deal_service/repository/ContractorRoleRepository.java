package com.example.deal_service.repository;

import com.example.deal_service.model.ContractorRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractorRoleRepository extends JpaRepository<ContractorRole, String> {

    Optional<ContractorRole> findByIdAndIsActiveTrue(String id);

}
