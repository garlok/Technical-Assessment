package com.ms.assessment.service;

import com.ms.assessment.model.Asset;
import com.ms.assessment.model.CryptoTradingDTO;
import com.ms.assessment.model.CryptoPricing;
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
import java.util.Optional;
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
        CryptoPricing cryptoPricing = pricingRepository.findBySymbol(tradeRequestDTO.getSymbol())
                .orElseThrow(() -> new NoSuchElementException("Symbol not found"));

        CryptoTradingDTO cryptoTradingDTO = CryptoTradingDTO.builder()
                .symbol(cryptoPricing.getSymbol())
                .bestBuyPrice(cryptoPricing.getBestBuyPrice())
                .bestSellPrice(cryptoPricing.getBestSellPrice())
                .buyQuantity(cryptoPricing.getBuyQuantity())
                .sellQuantity(cryptoPricing.getSellQuantity())
                .build();

        Wallet userWallet = walletRepository.findByUserUserName(tradeRequestDTO.getUserName())
                .orElseThrow(() -> new NoSuchElementException("User wallet not found"));
        List<Asset> assetList = assetRepository.findByUserUserName(tradeRequestDTO.getUserName());

        TradingInfoDTO tradingInfoDTO = TradingInfoDTO.builder()
                .requestSymbol(tradeRequestDTO.getSymbol())
                .requestUserName(tradeRequestDTO.getUserName())
                .build();
        TradingResponseDTO tradingResponseDTO = TradingResponseDTO.builder()
                .userName(tradingInfoDTO.getRequestUserName())
                .symbol(tradingInfoDTO.getRequestSymbol())
                .build();
        if (ActionType.BUY.equals(actionType)) {
            if(tradeRequestDTO.getBuyQuantity() == 0) {
                throw new Exception("Buy quantity cannot be zero.");
            }
            if(cryptoTradingDTO.getBuyQuantity() >= tradeRequestDTO.getBuyQuantity()) {
                tradingInfoDTO.setRequestQuantity(tradeRequestDTO.getBuyQuantity());
                tradingInfoDTO.setActionType(ActionType.BUY);
                tradingResponseDTO.setAssetsQuantity(tradeRequestDTO.getBuyQuantity());
                performOperation(tradingInfoDTO, cryptoTradingDTO, userWallet, assetList, tradingResponseDTO);
            } else {
                throw new Exception("Insufficient asset quantity to buy. Current quantity for "
                        + cryptoTradingDTO.getSymbol() + " is " + cryptoTradingDTO.getBuyQuantity());
            }
        } else if (ActionType.SELL.equals(actionType)){
            if(tradeRequestDTO.getSellQuantity() == 0) {
                throw new Exception("Sell quantity cannot be zero.");
            }
            if(cryptoTradingDTO.getSellQuantity() >= tradeRequestDTO.getSellQuantity()) {
                tradingInfoDTO.setRequestQuantity(tradeRequestDTO.getSellQuantity());
                tradingInfoDTO.setActionType(ActionType.SELL);
                tradingResponseDTO.setAssetsQuantity(tradeRequestDTO.getSellQuantity());
                performOperation(tradingInfoDTO, cryptoTradingDTO, userWallet, assetList, tradingResponseDTO);
            } else {
                throw new Exception("Insufficient asset quantity to sell. Current quantity for "
                        + cryptoTradingDTO.getSymbol() + " is " + cryptoTradingDTO.getSellQuantity());
            }
        }

        tradingResponseDTO.setMessage("Trade Successful");
        return tradingResponseDTO;
    }

    private void performOperation(TradingInfoDTO tradingInfoDTO, CryptoTradingDTO cryptoTradingDTO,
                                     Wallet userWallet, List<Asset> assetList,
                                     TradingResponseDTO tradingResponseDTO) throws Exception {
        BigDecimal requiredAmount = null;
        if(ActionType.BUY.equals(tradingInfoDTO.getActionType())){
            requiredAmount  = cryptoTradingDTO.getBestBuyPrice()
                    .multiply(BigDecimal.valueOf(tradingInfoDTO.getRequestQuantity()));
            if (userWallet.getBalance().compareTo(requiredAmount) < 0) {
                throw new Exception("Insufficient balance. Current wallet balance is "
                        + userWallet.getBalanceCurrency() + userWallet.getBalance().toPlainString()
                        + ". Still require " + userWallet.getBalanceCurrency()
                        + requiredAmount.subtract(userWallet.getBalance())
                );
            }
            tradingResponseDTO.setBuyPrice(cryptoTradingDTO.getBestBuyPrice());
            userWallet.setBalance(userWallet.getBalance().subtract(requiredAmount));
        } else {
            Optional<Asset> assetOptional = assetList.stream()
                    .filter(asset -> asset.getSymbol().equals(tradingInfoDTO.getRequestSymbol()))
                    .findFirst();

            if (!assetOptional.isPresent()) {
                throw new Exception("No asset to sell");
            } else if(assetOptional.get().getQuantity() < tradingInfoDTO.getRequestQuantity()){
                throw new Exception("Insufficient asset to sell. Current number owned asset(s) is/are " + assetOptional.get().getQuantity());
            }
            requiredAmount = cryptoTradingDTO.getBestSellPrice()
                    .multiply(BigDecimal.valueOf(tradingInfoDTO.getRequestQuantity()));
            tradingResponseDTO.setSellPrice(cryptoTradingDTO.getBestSellPrice());
            userWallet.setBalance(userWallet.getBalance().add(requiredAmount));
        }
        tradingResponseDTO.setTotalAmount(requiredAmount);
        tradingResponseDTO.setBalance(userWallet.getBalance());
        tradingResponseDTO.setBalanceCurrency(userWallet.getBalanceCurrency());
        updateAssetList(assetList, tradingInfoDTO);
        walletRepository.save(userWallet);
        assetRepository.saveAll(assetList);
    }

    private void updateAssetList(List<Asset> assetList, TradingInfoDTO tradingInfoDTO) {
        boolean assetFound = assetList.stream().filter(asset -> asset.getSymbol().equalsIgnoreCase(tradingInfoDTO.getRequestSymbol()))
                .findFirst()
                .map(asset -> {
                     int currentQuantity = asset.getQuantity();
                     int newQuantity = ActionType.BUY.equals(tradingInfoDTO.getActionType()) ?
                             currentQuantity + tradingInfoDTO.getRequestQuantity() :
                             currentQuantity - tradingInfoDTO.getRequestQuantity();
                     asset.setQuantity(newQuantity);
                     asset.setUpdatedTime(OffsetDateTime.now());
                     return true;
                })
                .orElse(false);
        if (!assetFound && ActionType.BUY.equals(tradingInfoDTO.getActionType())) {
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
