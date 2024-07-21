package com.ms.assessment.controller;

import com.ms.assessment.model.TradeHistoryRequestDTO;
import com.ms.assessment.model.TradeHistoryResponseDTO;
import com.ms.assessment.service.TradeHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ms.assessment.constants.ResourcePath.LISTING;
import static com.ms.assessment.constants.ResourcePath.TRADING_HISTORY;

@Tag(name = "Trading History API")
@RestController
@RequestMapping(TRADING_HISTORY)
public class TradeHistoryController {

    @Autowired
    TradeHistoryService tradeHistoryService;

    @PostMapping(value = LISTING)
    @Operation(summary = "To get trading history", description = "To get trading history")
    public ResponseEntity<List<TradeHistoryResponseDTO>> getTradeHistory(@RequestBody TradeHistoryRequestDTO tradeHistoryRequestDTO) {
        List<TradeHistoryResponseDTO> tradeHistoryResponseDTOList = tradeHistoryService.getTradeHistoryByUserName(tradeHistoryRequestDTO);
        return ResponseEntity.ok(tradeHistoryResponseDTOList);
    }
}
