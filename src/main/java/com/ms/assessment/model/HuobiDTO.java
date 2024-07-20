package com.ms.assessment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HuobiDTO {

    private String symbol;
    @JsonProperty("bid")
    private BigDecimal sell;
    @JsonProperty("ask")
    private BigDecimal buy;
}
