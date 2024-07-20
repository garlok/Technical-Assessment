package com.ms.assessment.repository;

import com.ms.assessment.model.Pricing;
import com.ms.assessment.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricingRepository extends JpaRepository<Pricing, UUID> {
    List<Pricing> findBySymbol(String symbol);
}
