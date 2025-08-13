package com.internship.deal_service.repository;

import com.internship.deal_service.event.Contractor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractorRepository extends JpaRepository<Contractor, String> {
}
