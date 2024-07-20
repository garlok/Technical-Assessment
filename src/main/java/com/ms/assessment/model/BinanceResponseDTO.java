package com.ms.assessment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BinanceResponseDTO {

    private String symbol;
    @JsonProperty("bidPrice")
    private BigDecimal sell;
    @JsonProperty("askPrice")
    private BigDecimal buy;
}
