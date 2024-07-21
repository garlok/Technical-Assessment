package com.ms.assessment.controller;

import com.ms.assessment.model.CryptoTradingDTO;
import com.ms.assessment.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ms.assessment.constants.ResourcePath.LISTING;
import static com.ms.assessment.constants.ResourcePath.PRICE;

@Tag(name = "Pricing API")
@RestController
@RequestMapping(PRICE)
public class PricingController {

    @Autowired
    PricingService pricingService;

    @GetMapping(LISTING)
    @Operation(summary = "To get latest Crypto Trading Listing with the best price",
            description = "To get latest Crypto Trading Listing with the best price")
    public ResponseEntity<?> getLatestBestPrice(@RequestParam(value = "cryptoTrading", required = false) String symbol) {
        try {
            List<CryptoTradingDTO> cryptoTradingDTOList = pricingService.getLatestBestAggregatedPrice(symbol);
            if(!cryptoTradingDTOList.isEmpty()){
                return ResponseEntity.ok(cryptoTradingDTOList);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Symbol not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
