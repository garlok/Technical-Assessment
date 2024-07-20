package com.ms.assessment.service;

import com.ms.assessment.model.BinanceResponseDTO;
import com.ms.assessment.model.Pricing;
import com.ms.assessment.model.HuobiResponseDTO;
import com.ms.assessment.repository.PricingRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.ms.assessment.constants.ResourcePath.BINANCE_API_URL;
import static com.ms.assessment.constants.ResourcePath.HUOBI_API_URL;

@Service
public class PricingService {

    @Autowired
    PricingRespository pricingRepository;

    public void fetchAndSavePricing() {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<BinanceResponseDTO>> binanceResponse = restTemplate.exchange(
                BINANCE_API_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BinanceResponseDTO>>() {
                });
        List<BinanceResponseDTO> binanceResponseDTOList = binanceResponse.getBody();

        ResponseEntity<HuobiResponseDTO> huobiResponse = restTemplate.exchange(
                HUOBI_API_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<HuobiResponseDTO>(){
                });
        HuobiResponseDTO huobiResponseDTO = huobiResponse.getBody();

        List<Pricing> existingPricings = pricingRepository.findAll();
        Map<String, Pricing> pricingMap = existingPricings.stream()
                .collect(Collectors.toMap(Pricing::getSymbol, p -> p));
        List<Pricing> updatedPricings = new ArrayList<>();

        Set<String> symbolsInApiResponses = new HashSet<>();

        if (binanceResponseDTOList != null && !binanceResponseDTOList.isEmpty()) {
            binanceResponseDTOList.forEach(binanceResponseDTO -> {
                String symbol = binanceResponseDTO.getSymbol().toUpperCase();
                symbolsInApiResponses.add(symbol);
                Pricing newPricing = Pricing.builder()
                        .symbol(symbol)
                        .bestAskPrice(binanceResponseDTO.getAskPrice())
                        .bestBidPrice(binanceResponseDTO.getBidPrice())
                        .build();

                Pricing existingPricing = pricingMap.get(symbol);
                if (existingPricing != null) {
                    updateBestPricing(existingPricing, newPricing.getBestBidPrice(), newPricing.getBestAskPrice());
                    updatedPricings.add(existingPricing);
                } else {
                    newPricing.setId(UUID.randomUUID());
                    updatedPricings.add(newPricing);
                }
            });
        }

        if (huobiResponseDTO != null && huobiResponseDTO.getData() != null) {
            huobiResponseDTO.getData().forEach(huobiDTO -> {
                String symbol = huobiDTO.getSymbol().toUpperCase();
                symbolsInApiResponses.add(symbol);
                Pricing newPricing = Pricing.builder()
                        .symbol(symbol)
                        .bestAskPrice(huobiDTO.getAsk())
                        .bestBidPrice(huobiDTO.getBid())
                        .build();

                Pricing existingPricing = updatedPricings.stream()
                        .filter(pricing -> pricing.getSymbol().equals(symbol))
                        .findFirst()
                        .orElse(null);
                if (existingPricing != null) {
                    updateBestPricing(existingPricing, newPricing.getBestBidPrice(), newPricing.getBestAskPrice());
                } else {
                    existingPricing = pricingMap.get(symbol);
                    if (existingPricing != null) {
                        updateBestPricing(existingPricing, newPricing.getBestBidPrice(), newPricing.getBestAskPrice());
                    } else {
                        newPricing.setId(UUID.randomUUID()); // Set a new ID for new entries
                        updatedPricings.add(newPricing);
                    }
                }
            });
        }
        List<Pricing> toRemove = existingPricings.stream()
                .filter(pricing -> !symbolsInApiResponses.contains(pricing.getSymbol()))
                .collect(Collectors.toList());

        // Save to the database
        pricingRepository.saveAll(updatedPricings);
        pricingRepository.deleteAll(toRemove);
    }

    private void updateBestPricing(Pricing existingDTO, BigDecimal newBidPrice,
                                   BigDecimal newAskPrice) {
        if (newBidPrice.compareTo(existingDTO.getBestBidPrice()) > 0) {
            existingDTO.setBestBidPrice(newBidPrice);
        }
        if (newAskPrice.compareTo(existingDTO.getBestAskPrice()) < 0) {
            existingDTO.setBestAskPrice(newAskPrice);
        }
    }
}