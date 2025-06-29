package com.example.deal_service.repository;

import com.example.deal_service.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DealRepository extends JpaRepository<Deal, UUID> {

    Optional<Deal> findByIdAndIsActiveTrue(UUID id);

}
