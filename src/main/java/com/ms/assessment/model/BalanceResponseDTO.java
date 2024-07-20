package com.ms.assessment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Getter
@Service
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponseDTO {

    @JsonProperty(value = "User Name")
    private String userName;
    @JsonProperty(value = "Balance")
    private BigDecimal balance;
    @JsonProperty(value = "Currency")
    private String currency;
}
