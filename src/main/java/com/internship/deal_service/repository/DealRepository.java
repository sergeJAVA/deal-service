package com.internship.deal_service.repository;

import com.internship.deal_service.model.Deal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для сущности {@link Deal}.
 */
public interface DealRepository extends JpaRepository<Deal, UUID>, JpaSpecificationExecutor<Deal> {

    Optional<Deal> findByIdAndIsActiveTrue(UUID id);

    @EntityGraph(attributePaths = {
            "type",
            "status",
            "dealSums",
            "dealSums.currency",
            "dealContractors",
            "dealContractors.roles",
            "dealContractors.roles.role"

    })
    Page<Deal> findAll(Specification<Deal> spec, Pageable pageable);

}
