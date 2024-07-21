package com.ms.assessment.service;

import com.ms.assessment.model.BinanceResponseDTO;
import com.ms.assessment.model.CryptoTradingDTO;
import com.ms.assessment.model.HuobiDTO;
import com.ms.assessment.model.HuobiResponseDTO;
import com.ms.assessment.model.Pricing;
import com.ms.assessment.repository.PricingRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ms.assessment.constants.ResourcePath.BINANCE_API_URL;
import static com.ms.assessment.constants.ResourcePath.HUOBI_API_URL;

@Slf4j
@Service
public class PricingService {

    @Autowired
    PricingRepository pricingRepository;

    public void fetchAndSavePricing() {
        RestTemplate restTemplate = new RestTemplate();
        List<BinanceResponseDTO> binanceResponseDTOList = fetchBinanceData(restTemplate);
        HuobiResponseDTO huobiResponseDTO = fetchHuobiData(restTemplate);

        List<Pricing> existingPricings = pricingRepository.findAll();
        Map<String, Pricing> pricingMap = existingPricings.stream()
                .collect(Collectors.toMap(Pricing::getSymbol, p -> p));
        log.debug("[pricingService] fetchAndSavePricing pricingMap.size(): {}", pricingMap.size());

        List<Pricing> updatedPricings = new ArrayList<>();
        Set<String> symbolsInApiResponses = new HashSet<>();

        //To include all binance list to a lists
        if (binanceResponseDTOList != null && !binanceResponseDTOList.isEmpty()) {
            binanceResponseDTOList.forEach(binanceResponseDTO -> {
                String symbol = binanceResponseDTO.getSymbol().toUpperCase();
                symbolsInApiResponses.add(symbol);
                Pricing existingPricing = pricingMap.get(symbol);
                if (existingPricing != null) {
                    existingPricing.setBestSellPrice(binanceResponseDTO.getSell());
                    existingPricing.setBestBuyPrice(binanceResponseDTO.getBuy());
                    existingPricing.setSellQuantity(binanceResponseDTO.getSellQuantity().intValue());
                    existingPricing.setBuyQuantity(binanceResponseDTO.getBuyQuantity().intValue());
                    updatedPricings.add(existingPricing);
                } else {
                    //To add a new one into the lists
                    Pricing newPricing = getNewPricing(binanceResponseDTO, null);
                    updatedPricings.add(newPricing);
                }
            });
        }
        //To all Huobi list to a lists
        if (huobiResponseDTO != null && huobiResponseDTO.getData() != null) {
            huobiResponseDTO.getData().forEach(huobiDTO -> {
                String symbol = huobiDTO.getSymbol().toUpperCase();

                //To check huobi list is existed in the list
                Pricing existingPricing = updatedPricings.stream()
                        .filter(pricing -> pricing.getSymbol().equals(symbol))
                        .findFirst()
                        .orElse(null);
                if (existingPricing != null) {
                    //check the existing price and update
                    updateBestPricing(existingPricing, huobiDTO.getSell(), huobiDTO.getBuy());
                    existingPricing.setBuyQuantity(huobiDTO.getBuyQuantity().intValue());
                    existingPricing.setSellQuantity(huobiDTO.getSellQuantity().intValue());

                } else {
                    symbolsInApiResponses.add(symbol);
                    existingPricing = pricingMap.get(symbol);
                    if (existingPricing != null) {
                        existingPricing.setBestBuyPrice(huobiDTO.getBuy());
                        existingPricing.setBestSellPrice(huobiDTO.getSell());
                        existingPricing.setSellQuantity(huobiDTO.getSellQuantity().intValue());
                        existingPricing.setBuyQuantity(huobiDTO.getBuyQuantity().intValue());
                    } else {
                        Pricing newPricing = getNewPricing(null, huobiDTO);
                        updatedPricings.add(newPricing);
                    }
                }
            });
        }
        List<Pricing> toRemove = existingPricings.stream()
                .filter(pricing -> !symbolsInApiResponses.contains(pricing.getSymbol()))
                .toList();
        log.debug("[pricingService] fetchAndSavePricing symbolsInApiResponses.size(): {}", symbolsInApiResponses.size());

        // Save to the database
        log.info("[pricingService] fetchAndSavePricing updatedPricings.size(): {}", updatedPricings.size());
        pricingRepository.saveAll(updatedPricings);

        //To delete in the database
        log.info("[pricingService] toRemove.size(): {}", toRemove.size());
    }

    public List<CryptoTradingDTO> getLatestBestAggregatedPrice(String symbol) {
        List<Pricing> pricingList = new ArrayList<>();
        List<CryptoTradingDTO> cryptoTradingDTOList = new ArrayList<>();

        if (ObjectUtils.isNotEmpty(symbol)) {
             Pricing pricing = pricingRepository.findBySymbol(symbol)
                    .orElseThrow(() -> new NoSuchElementException("Symbol not found"));
            pricingList.add(pricing);
        } else {
            pricingList = pricingRepository.findAll();
        }

        if (!pricingList.isEmpty()) {
            pricingList.forEach(pricing -> {
                CryptoTradingDTO cryptoTradingDTO = CryptoTradingDTO.builder()
                        .symbol(pricing.getSymbol())
                        .bestSellPrice(pricing.getBestSellPrice())
                        .bestBuyPrice(pricing.getBestBuyPrice())
                        .buyQuantity(pricing.getBuyQuantity())
                        .sellQuantity(pricing.getSellQuantity())
                        .build();
                cryptoTradingDTOList.add(cryptoTradingDTO);
            });
        }
        return cryptoTradingDTOList;
    }

    private List<BinanceResponseDTO> fetchBinanceData(RestTemplate restTemplate) {
        try {
            ResponseEntity<List<BinanceResponseDTO>> response = restTemplate.exchange(
                    BINANCE_API_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch Binance data", e);
            return Collections.emptyList();
        }
    }

    private HuobiResponseDTO fetchHuobiData(RestTemplate restTemplate) {
        try {
            ResponseEntity<HuobiResponseDTO> response = restTemplate.exchange(
                    HUOBI_API_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch Huobi data", e);
            return null;
        }
    }

    private static Pricing getNewPricing(BinanceResponseDTO binanceResponseDTO, HuobiDTO huobiDTO) {
        String symbol = "";
        BigDecimal buyPrice = null;
        BigDecimal sellPrice = null;
        int buyQuantity = 0;
        int sellQuantity = 0;

        if(binanceResponseDTO != null){
            symbol = binanceResponseDTO.getSymbol().toUpperCase();
            buyPrice = binanceResponseDTO.getBuy();
            sellPrice = binanceResponseDTO.getSell();
            buyQuantity = binanceResponseDTO.getBuyQuantity().intValue();
            sellQuantity = binanceResponseDTO.getSellQuantity().intValue();
        } else if (huobiDTO != null){
            symbol = huobiDTO.getSymbol().toUpperCase();
            buyPrice = huobiDTO.getBuy();
            sellPrice = huobiDTO.getSell();
            buyQuantity = huobiDTO.getBuyQuantity().intValue();
            sellQuantity = huobiDTO.getSellQuantity().intValue();
        }

        return Pricing.builder()
                .id(UUID.randomUUID())
                .symbol(symbol)
                .bestBuyPrice(buyPrice)
                .bestSellPrice(sellPrice)
                .buyQuantity(buyQuantity)
                .sellQuantity(sellQuantity)
                .build();
    }

    private void updateBestPricing(Pricing existingDTO, BigDecimal newBidPrice,
                                   BigDecimal newAskPrice) {
        if (existingDTO.getBestSellPrice() == null || newBidPrice != null && newBidPrice.compareTo(existingDTO.getBestSellPrice())  > 0) {
            existingDTO.setBestSellPrice(newBidPrice);
        }
        if (existingDTO.getBestBuyPrice() == null || newAskPrice != null && newAskPrice.compareTo(existingDTO.getBestBuyPrice()) < 0) {
            existingDTO.setBestBuyPrice(newAskPrice);
        }
    }
}