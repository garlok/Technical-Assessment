package com.ms.assessment.repository;

import com.ms.assessment.model.TradeHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TradeHistoryRepository extends JpaRepository<TradeHistory, UUID> {
    List<TradeHistory> findByUserUserName(String userName, Sort sort);
}
