package com.ms.assessment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class AssetDTO {

    @JsonProperty(value = "Symbol")
    private String symbol;

    @JsonProperty(value = "Quantity")
    private int quantity;

    @JsonProperty(value = "Updated Time")
    private OffsetDateTime updatedTime;
}
