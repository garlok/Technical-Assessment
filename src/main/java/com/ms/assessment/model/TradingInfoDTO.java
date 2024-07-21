package com.ms.assessment.model;

import com.ms.assessment.model.enums.ActionType;
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
    private ActionType actionType;
}
