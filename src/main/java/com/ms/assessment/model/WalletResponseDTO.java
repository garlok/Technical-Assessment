package com.ms.assessment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Service
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletResponseDTO {

    @JsonProperty(value = "User Name")
    private String userName;

    @JsonProperty(value = "Balance")
    private BigDecimal balance;

    @JsonProperty(value = "Balance Currency")
    private String currency;

    @JsonProperty(value = "Asset List")
    private List<AssetDTO> assetList;
}
