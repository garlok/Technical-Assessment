package com.ms.assessment.service;

import com.ms.assessment.model.BinanceResponseDTO;
import com.ms.assessment.model.CryptoTradingDTO;
import com.ms.assessment.model.HuobiDTO;
import com.ms.assessment.model.HuobiResponseDTO;
import com.ms.assessment.model.CryptoPricing;
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

        List<CryptoPricing> existingCryptoPricings = pricingRepository.findAll();
        Map<String, CryptoPricing> pricingMap = existingCryptoPricings.stream()
                .collect(Collectors.toMap(CryptoPricing::getSymbol, p -> p));
        log.debug("[pricingService] fetchAndSavePricing pricingMap.size(): {}", pricingMap.size());

        List<CryptoPricing> updatedCryptoPricings = new ArrayList<>();
        Set<String> symbolsInApiResponses = new HashSet<>();

        //To include all binance list to a lists
        if (binanceResponseDTOList != null && !binanceResponseDTOList.isEmpty()) {
            binanceResponseDTOList.forEach(binanceResponseDTO -> {
                String symbol = binanceResponseDTO.getSymbol().toUpperCase();
                symbolsInApiResponses.add(symbol);
                CryptoPricing existingCryptoPricing = pricingMap.get(symbol);
                if (existingCryptoPricing != null) {
                    existingCryptoPricing.setBestSellPrice(binanceResponseDTO.getSell());
                    existingCryptoPricing.setBestBuyPrice(binanceResponseDTO.getBuy());
                    existingCryptoPricing.setSellQuantity(binanceResponseDTO.getSellQuantity().intValue());
                    existingCryptoPricing.setBuyQuantity(binanceResponseDTO.getBuyQuantity().intValue());
                    updatedCryptoPricings.add(existingCryptoPricing);
                } else {
                    //To add a new one into the lists
                    CryptoPricing newCryptoPricing = getNewPricing(binanceResponseDTO, null);
                    updatedCryptoPricings.add(newCryptoPricing);
                }
            });
        }
        //To all Huobi list to a lists
        if (huobiResponseDTO != null && huobiResponseDTO.getData() != null) {
            huobiResponseDTO.getData().forEach(huobiDTO -> {
                String symbol = huobiDTO.getSymbol().toUpperCase();

                //To check huobi list is existed in the list
                CryptoPricing existingCryptoPricing = updatedCryptoPricings.stream()
                        .filter(cryptoPricing -> cryptoPricing.getSymbol().equals(symbol))
                        .findFirst()
                        .orElse(null);
                if (existingCryptoPricing != null) {
                    //check the existing price from binance and update
                    updateBestPricingAndQuantity(existingCryptoPricing, huobiDTO.getSell(), huobiDTO.getBuy(),
                            huobiDTO.getBuyQuantity().intValue(), huobiDTO.getSellQuantity().intValue());

                } else {
                    symbolsInApiResponses.add(symbol);
                    existingCryptoPricing = pricingMap.get(symbol);
                    if (existingCryptoPricing != null) {
                        existingCryptoPricing.setBestBuyPrice(huobiDTO.getBuy());
                        existingCryptoPricing.setBestSellPrice(huobiDTO.getSell());
                        existingCryptoPricing.setSellQuantity(huobiDTO.getSellQuantity().intValue());
                        existingCryptoPricing.setBuyQuantity(huobiDTO.getBuyQuantity().intValue());
                    } else {
                        CryptoPricing newCryptoPricing = getNewPricing(null, huobiDTO);
                        updatedCryptoPricings.add(newCryptoPricing);
                    }
                }
            });
        }
        List<CryptoPricing> toRemove = existingCryptoPricings.stream()
                .filter(cryptoPricing -> !symbolsInApiResponses.contains(cryptoPricing.getSymbol()))
                .toList();
        log.debug("[pricingService] fetchAndSavePricing symbolsInApiResponses.size(): {}", symbolsInApiResponses.size());

        // Save to the database
        log.info("[pricingService] fetchAndSavePricing updatedPricings.size(): {}", updatedCryptoPricings.size());
        pricingRepository.saveAll(updatedCryptoPricings);

        //To delete in the database
        log.info("[pricingService] toRemove.size(): {}", toRemove.size());
    }

    public List<CryptoTradingDTO> getLatestBestAggregatedPrice(String symbol) {
        List<CryptoPricing> cryptoPricingList = new ArrayList<>();
        List<CryptoTradingDTO> cryptoTradingDTOList = new ArrayList<>();

        if (ObjectUtils.isNotEmpty(symbol)) {
             CryptoPricing cryptoPricing = pricingRepository.findBySymbol(symbol)
                    .orElseThrow(() -> new NoSuchElementException("Symbol not found"));
            cryptoPricingList.add(cryptoPricing);
        } else {
            cryptoPricingList = pricingRepository.findAll();
        }

        if (!cryptoPricingList.isEmpty()) {
            cryptoPricingList.forEach(cryptoPricing -> {
                CryptoTradingDTO cryptoTradingDTO = CryptoTradingDTO.builder()
                        .symbol(cryptoPricing.getSymbol())
                        .bestSellPrice(cryptoPricing.getBestSellPrice())
                        .bestBuyPrice(cryptoPricing.getBestBuyPrice())
                        .buyQuantity(cryptoPricing.getBuyQuantity())
                        .sellQuantity(cryptoPricing.getSellQuantity())
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

    private static CryptoPricing getNewPricing(BinanceResponseDTO binanceResponseDTO, HuobiDTO huobiDTO) {
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

        return CryptoPricing.builder()
                .id(UUID.randomUUID())
                .symbol(symbol)
                .bestBuyPrice(buyPrice)
                .bestSellPrice(sellPrice)
                .buyQuantity(buyQuantity)
                .sellQuantity(sellQuantity)
                .build();
    }

    private void updateBestPricingAndQuantity(CryptoPricing existingDTO, BigDecimal newBidPrice,
                                              BigDecimal newAskPrice, int newBuyQuantity, int newSellQuantity) {
        if (existingDTO.getBestSellPrice() == null || newBidPrice != null && newBidPrice.compareTo(existingDTO.getBestSellPrice())  > 0) {
            existingDTO.setBestSellPrice(newBidPrice);
        }
        if (existingDTO.getBestBuyPrice() == null || newAskPrice != null && newAskPrice.compareTo(existingDTO.getBestBuyPrice()) < 0) {
            existingDTO.setBestBuyPrice(newAskPrice);
        }
        if (newBuyQuantity > (existingDTO.getBuyQuantity())) {
            existingDTO.setBuyQuantity(newBuyQuantity);
        }
        if (newSellQuantity > existingDTO.getSellQuantity()) {
            existingDTO.setSellQuantity(newSellQuantity);
        }
    }
}