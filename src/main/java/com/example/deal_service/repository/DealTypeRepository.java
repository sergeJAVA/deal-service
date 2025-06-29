package com.example.deal_service.repository;

import com.example.deal_service.model.DealType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DealTypeRepository extends JpaRepository<DealType, String> {

    Optional<DealType> findByIdAndIsActiveTrue(String id);

}
