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
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class TradeHistoryResponseDTO {

    @JsonProperty(value = "User Name")
    private String userName;


    @JsonProperty(value = "Action Type")
    private String actionType;

    @JsonProperty(value = "Symbol")
    private String symbol;

    @JsonProperty(value = "Currency")
    private String currency;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    @JsonProperty(value = "Amount")
    private BigDecimal amount;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    @JsonProperty(value = "Balance")
    private BigDecimal balance;

    @JsonProperty(value = "Quantity")
    private int quantity;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonDeserialize(using = BigDecimalDeserializer.class)
    @JsonProperty(value = "Symbol Price")
    private BigDecimal symbolPrice;

    @JsonProperty(value = "Trade Time")
    private OffsetDateTime tradeTime;

}
