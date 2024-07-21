package com.ms.assessment.repository;

import com.ms.assessment.model.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, UUID> {
    Optional<Pricing> findBySymbol(String symbol);
}
