package com.ms.assessment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ms.assessment.config.BigDecimalDeserializer;
import com.ms.assessment.config.BigDecimalSerializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HuobiDTO {

    private String symbol;
    @JsonProperty("bid")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal sell;
    @JsonProperty("ask")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal buy;
}
