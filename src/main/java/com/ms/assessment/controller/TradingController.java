package com.ms.assessment.controller;


import com.ms.assessment.model.TradeRequestDTO;
import com.ms.assessment.model.TradingResponseDTO;
import com.ms.assessment.model.enums.ActionType;
import com.ms.assessment.service.TradingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ms.assessment.constants.ResourcePath.PERFORM;
import static com.ms.assessment.constants.ResourcePath.TRADING;

@Tag(name = "Trading API")
@RestController
@RequestMapping(TRADING)
public class TradingController {

    @Autowired
    TradingService tradingService;

    @PostMapping(PERFORM)
    @Operation(summary = "To perform trading actions", description = "To perform trading actions")
    public ResponseEntity<?> performTrade(@RequestBody TradeRequestDTO tradeRequestDTO, @PathVariable ActionType actionType) {
        try {
            TradingResponseDTO tradingResponseDTO = tradingService.performTrade(tradeRequestDTO, actionType);
            return ResponseEntity.ok(tradingResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
