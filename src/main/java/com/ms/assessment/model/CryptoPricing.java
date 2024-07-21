package com.ms.assessment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String symbol;

    @Column(precision = 30, scale = 8)
    private BigDecimal bestSellPrice;

    @Column(precision = 30, scale = 8)
    private BigDecimal bestBuyPrice;

    private int sellQuantity;
    private int buyQuantity;
}
