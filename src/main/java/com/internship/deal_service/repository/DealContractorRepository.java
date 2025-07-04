package com.internship.deal_service.repository;

import com.internship.deal_service.model.DealContractor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для сущности {@link DealContractor}.
 */
public interface DealContractorRepository extends JpaRepository<DealContractor, UUID> {

    Optional<DealContractor> findByIdAndIsActiveTrue(UUID id);

    List<DealContractor> findByDealIdAndIsActiveTrue(UUID dealId);

    @Modifying
    @Transactional
    @Query("UPDATE DealContractor dc SET dc.main = false WHERE dc.deal.id = :dealId AND (:currentContractorId IS NULL OR dc.id <> :currentContractorId)")
    void updateAllOthersMainToFalseForDeal(@Param("dealId") UUID dealId, @Param("currentContractorId") UUID currentContractorId);

    List<DealContractor> findAllByDealIdAndIsActiveTrue(UUID dealId);

}
