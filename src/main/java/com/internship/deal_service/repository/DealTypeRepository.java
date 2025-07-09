package com.internship.deal_service.repository;

import com.internship.deal_service.model.DealType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для сущности {@link DealType}.
 */
public interface DealTypeRepository extends JpaRepository<DealType, String> {

    Optional<DealType> findByIdAndIsActiveTrue(String id);

}
