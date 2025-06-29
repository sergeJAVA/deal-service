package com.example.deal_service.repository;

import com.example.deal_service.model.DealStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DealStatusRepository extends JpaRepository<DealStatus, String> {

    Optional<DealStatus> findByIdAndIsActiveTrue(String id);

}
