package com.ms.assessment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class Pricing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String symbol;
    private BigDecimal bestSellPrice;
    private BigDecimal bestBuyPrice;
}
