package com.ms.assessment.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TradeRequestDTO {
    private String userName;
    private String symbol;
    private int buyQuantity;
    private int sellQuantity;
}
