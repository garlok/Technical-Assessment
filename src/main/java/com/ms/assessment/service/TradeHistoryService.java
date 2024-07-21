package com.ms.assessment.service;

import com.ms.assessment.model.TradeHistory;
import com.ms.assessment.model.TradeHistoryRequestDTO;
import com.ms.assessment.model.TradeHistoryResponseDTO;
import com.ms.assessment.repository.TradeHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.ms.assessment.constants.Constants.TRADE_TIME;

@Slf4j
@Service
public class TradeHistoryService {

    @Autowired
    TradeHistoryRepository tradeHistoryRepository;

    public List<TradeHistoryResponseDTO> getTradeHistoryByUserName(TradeHistoryRequestDTO tradeHistoryRequestDTO) {
        Sort sort = Sort.by(Sort.Order.desc(TRADE_TIME));
        List<TradeHistory> tradeHistoryList = tradeHistoryRepository.findByUserUserName(tradeHistoryRequestDTO.getUserName(), sort);
        List<TradeHistoryResponseDTO> tradeHistoryResponseDTOList = new ArrayList<>();
        tradeHistoryList.forEach(tradeHistory -> {
            TradeHistoryResponseDTO tradeHistoryResponseDTO = TradeHistoryResponseDTO.builder()
                    .userName(tradeHistory.getUser().getUserName())
                    .actionType(tradeHistory.getActionType())
                    .symbol(tradeHistory.getSymbol())
                    .currency(tradeHistory.getCurrency())
                    .amount(tradeHistory.getAmount())
                    .balance(tradeHistory.getBalance())
                    .quantity(tradeHistory.getQuantity())
                    .symbolPrice(tradeHistory.getSymbolPrice())
                    .tradeTime(tradeHistory.getTradeTime())
                    .build();
            tradeHistoryResponseDTOList.add(tradeHistoryResponseDTO);
        });
        return tradeHistoryResponseDTOList;
    }
}
