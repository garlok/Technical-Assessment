package com.ms.assessment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TradingResponseDTO {

    @JsonProperty(value = "User Name")
    private String userName;

    @JsonProperty(value = "Symbol")
    private String symbol;

    @JsonProperty(value = "Buy/Ask Price")
    private BigDecimal buyPrice;

    @JsonProperty(value = "Sell/Bid Price")
    private BigDecimal sellPrice;

    @JsonProperty(value = "Asset Quantity Buy/Sell")
    private int assetsQuantity;

    @JsonProperty(value = "Total Amount Spent")
    private BigDecimal totalAmount;

    @JsonProperty(value = "Wallet Balance")
    private BigDecimal balance;

    @JsonProperty(value = "Wallet Balance Currency")
    private String balanceCurrency;

    @JsonProperty(value = "Message")
    private String message;
}
