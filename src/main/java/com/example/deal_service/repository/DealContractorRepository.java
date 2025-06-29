package com.example.deal_service.repository;

import com.example.deal_service.model.DealContractor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DealContractorRepository extends JpaRepository<DealContractor, UUID> {

    Optional<DealContractor> findByIdAndIsActiveTrue(UUID id);

    List<DealContractor> findByDealIdAndIsActiveTrue(UUID dealId);

}
