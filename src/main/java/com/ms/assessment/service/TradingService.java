package com.ms.assessment.service;

import com.ms.assessment.model.Asset;
import com.ms.assessment.model.CryptoTradingDTO;
import com.ms.assessment.model.Pricing;
import com.ms.assessment.model.TradeRequestDTO;
import com.ms.assessment.model.TradingInfoDTO;
import com.ms.assessment.model.TradingResponseDTO;
import com.ms.assessment.model.Wallet;
import com.ms.assessment.model.enums.ActionType;
import com.ms.assessment.repository.AssetRepository;
import com.ms.assessment.repository.PricingRepository;
import com.ms.assessment.repository.UsersRepository;
import com.ms.assessment.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
public class TradingService {

    @Autowired
    PricingRepository pricingRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    UsersRepository usersRepository;
    public TradingResponseDTO performTrade(TradeRequestDTO tradeRequestDTO, ActionType actionType) throws Exception {
        Pricing pricing = pricingRepository.findBySymbol(tradeRequestDTO.getSymbol())
                .orElseThrow(() -> new NoSuchElementException("Symbol not found"));

        CryptoTradingDTO cryptoTradingDTO = CryptoTradingDTO.builder()
                .symbol(pricing.getSymbol())
                .bestBuyPrice(pricing.getBestBuyPrice())
                .bestSellPrice(pricing.getBestSellPrice())
                .buyQuantity(pricing.getBuyQuantity())
                .sellQuantity(pricing.getSellQuantity())
                .build();

        Wallet userWallet = walletRepository.findByUserUserName(tradeRequestDTO.getUserName())
                .orElseThrow(() -> new NoSuchElementException("User wallet not found"));
        List<Asset> assetList = assetRepository.findByUserUserName(tradeRequestDTO.getUserName());

        TradingInfoDTO tradingInfoDTO = TradingInfoDTO.builder()
                .requestSymbol(tradeRequestDTO.getSymbol())
                .requestQuantity(ActionType.BUY.equals(actionType) ? tradeRequestDTO.getBuyQuantity() : tradeRequestDTO.getSellQuantity())
                .requestUserName(tradeRequestDTO.getUserName())
                .build();
        TradingResponseDTO tradingResponseDTO = TradingResponseDTO.builder()
                .userName(tradingInfoDTO.getRequestUserName())
                .assetsQuantity(ActionType.BUY.equals(actionType) ? tradeRequestDTO.getBuyQuantity() : tradeRequestDTO.getSellQuantity())
                .symbol(tradingInfoDTO.getRequestSymbol())
                .build();
        if (ActionType.BUY.equals(actionType)) {
            if(cryptoTradingDTO.getBuyQuantity() >= tradeRequestDTO.getBuyQuantity()) {
                performBuyOperation(tradingInfoDTO, cryptoTradingDTO, userWallet, assetList, tradingResponseDTO);
            } else {
                throw new Exception("Insufficient asset quantity to buy. Current quantity for "
                        + cryptoTradingDTO.getSymbol() + " is " + cryptoTradingDTO.getBuyQuantity());
            }
        }
        tradingResponseDTO.setMessage("Trade Successful");
        return tradingResponseDTO;
    }

    private void performBuyOperation(TradingInfoDTO tradingInfoDTO, CryptoTradingDTO cryptoTradingDTO,
                                     Wallet userWallet, List<Asset> assetList, TradingResponseDTO tradingResponseDTO) throws Exception {
        BigDecimal requiredAmount = cryptoTradingDTO.getBestBuyPrice()
                .multiply(BigDecimal.valueOf(tradingInfoDTO.getRequestQuantity()));
        if (userWallet.getBalance().compareTo(requiredAmount) < 0) {
            throw new Exception("Insufficient balance. Current wallet balance is "
                    + userWallet.getBalance().toPlainString());
        } else {
            userWallet.setBalance(userWallet.getBalance().subtract(requiredAmount));
            updateAssetList(assetList, tradingInfoDTO);
            walletRepository.save(userWallet);
            assetRepository.saveAll(assetList);
            tradingResponseDTO.setBuyPrice(cryptoTradingDTO.getBestBuyPrice());
            tradingResponseDTO.setTotalAmount(requiredAmount);
            tradingResponseDTO.setBalance(userWallet.getBalance());
            tradingResponseDTO.setBalanceCurrency(userWallet.getCurrency());
        }
    }

    private void updateAssetList(List<Asset> assetList, TradingInfoDTO tradingInfoDTO) {
        boolean assetFound = assetList.stream().filter(asset -> asset.getSymbol().equalsIgnoreCase(tradingInfoDTO.getRequestSymbol()))
                .findFirst()
                .map(asset -> {
                     int currentQuantity = asset.getQuantity();
                     int newQuantity = currentQuantity + tradingInfoDTO.getRequestQuantity();
                     asset.setQuantity(newQuantity);
                     asset.setUpdatedTime(OffsetDateTime.now());
                     return true;
                })
                .orElse(false);
        if (!assetFound) {
            Asset newAsset = Asset.builder()
                    .id(UUID.randomUUID())
                    .symbol(tradingInfoDTO.getRequestSymbol())
                    .quantity(tradingInfoDTO.getRequestQuantity())
                    .user(usersRepository.findByUserName(tradingInfoDTO.getRequestUserName()))
                    .updatedTime(OffsetDateTime.now()).build();
            assetList.add(newAsset);
        }
    }
}
