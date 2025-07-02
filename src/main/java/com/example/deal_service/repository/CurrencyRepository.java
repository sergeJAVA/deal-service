package com.example.deal_service.repository;

import com.example.deal_service.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для сущности {@link Currency}.
 */
public interface CurrencyRepository extends JpaRepository<Currency, String> {

    Optional<Currency> findByIdAndIsActiveTrue(String id);

}
