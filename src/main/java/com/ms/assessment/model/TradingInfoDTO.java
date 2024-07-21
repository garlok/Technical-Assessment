package com.ms.assessment.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TradingInfoDTO {

    private String requestSymbol;
    private int requestQuantity;
    private String requestUserName;
}
