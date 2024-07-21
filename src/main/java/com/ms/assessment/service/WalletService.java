package com.ms.assessment.service;

import com.ms.assessment.model.Asset;
import com.ms.assessment.model.AssetDTO;
import com.ms.assessment.model.WalletResponseDTO;
import com.ms.assessment.model.Wallet;
import com.ms.assessment.repository.AssetRepository;
import com.ms.assessment.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class WalletService {
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    AssetRepository assetRepository;

    public WalletResponseDTO getWalletByUserName(String userName) {
        try{
            Optional<Wallet> wallets = walletRepository.findByUserUserName(userName);
            List<Asset> assetList = assetRepository.findByUserUserName(userName);
            List<AssetDTO> assetDTOList = new ArrayList<>();
            assetList.forEach(asset -> {
                AssetDTO assetDTO = AssetDTO.builder()
                        .symbol(asset.getSymbol())
                        .quantity(asset.getQuantity())
                        .updatedTime(asset.getUpdatedTime())
                        .build();
                assetDTOList.add(assetDTO);
            });
            if(wallets.isPresent()){
                return WalletResponseDTO.builder()
                        .userName(wallets.get().getUser().getUserName())
                        .balance(wallets.get().getBalance())
                        .currency(wallets.get().getBalanceCurrency())
                        .assetList(assetDTOList.isEmpty() ? null : assetDTOList)
                        .build();
            } else{
                throw new NoSuchElementException("User wallet not found");
            }
        } catch (Exception exception){
            log.error("User wallet not found", exception);
            throw exception;
        }
    }
}
