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
public class BinanceResponseDTO {

    private String symbol;
    @JsonProperty("bidPrice")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal sell;

    @JsonProperty("bidQty")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal sellQuantity;

    @JsonProperty("askPrice")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal buy;

    @JsonProperty("askQty")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal buyQuantity;
}
