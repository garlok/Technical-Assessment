package com.ms.assessment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ms.assessment.config.BigDecimalDeserializer;
import com.ms.assessment.config.BigDecimalSerializer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TradingResponseDTO {

    @JsonProperty(value = "User Name")
    private String userName;

    @JsonProperty(value = "Symbol")
    private String symbol;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    @JsonProperty(value = "Buy/Ask Price")
    private BigDecimal buyPrice;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    @JsonProperty(value = "Sell/Bid Price")
    private BigDecimal sellPrice;

    @JsonProperty(value = "Asset Buy/Ask Quantity")
    private int assetsBuyQuantity;

    @JsonProperty(value = "Asset Sell/Bid Quantity")
    private int assetsSellQuantity;

    @JsonProperty(value = "Currency")
    private String currency;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    @JsonProperty(value = "Amount Spent")
    private BigDecimal amountSpent;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    @JsonProperty(value = "Wallet Balance")
    private BigDecimal balance;

    @JsonProperty(value = "Message")
    private String message;

    @JsonProperty(value = "Trade time")
    private OffsetDateTime tradeTime;
}
