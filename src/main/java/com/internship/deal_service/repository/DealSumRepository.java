package com.internship.deal_service.repository;

import com.internship.deal_service.model.Deal;
import com.internship.deal_service.model.DealSum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для сущности {@link DealSum}.
 */
public interface DealSumRepository extends JpaRepository<DealSum, Long> {

    Optional<DealSum> findByIdAndIsActiveTrue(Long id);

    Optional<DealSum> findByDealAndIsMainTrueAndIsActiveTrue(Deal deal);

    List<DealSum> findByDealAndIsActiveTrue(Deal deal);

    Optional<DealSum> findByDealIdAndIsMainTrueAndIsActiveTrue(UUID dealId);

}
