package com.ms.assessment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ms.assessment.config.BigDecimalDeserializer;
import com.ms.assessment.config.BigDecimalSerializer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CryptoTradingDTO {

    @JsonProperty(value = "Crypto Trading")
    private String symbol;
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    @JsonProperty(value = "Best Bid/Sell Price")
    private BigDecimal bestSellPrice;
    @JsonProperty(value = "Best Ask/Buy Price")
    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    private BigDecimal bestBuyPrice;
}