package com.ms.assessment.repository;

import com.ms.assessment.model.CryptoPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricingRepository extends JpaRepository<CryptoPricing, UUID> {
    Optional<CryptoPricing> findBySymbol(String symbol);
}
