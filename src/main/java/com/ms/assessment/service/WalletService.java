package com.ms.assessment.service;

import com.ms.assessment.model.BalanceResponseDTO;
import com.ms.assessment.model.Wallet;
import com.ms.assessment.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class WalletService {
    @Autowired
    WalletRepository walletRepository;

    public BalanceResponseDTO getBalanceByUserName(String userName) throws Exception {
        try{
            Optional<Wallet> wallets = walletRepository.findByUserUserName(userName);
            if(wallets.isPresent()){
                return BalanceResponseDTO.builder()
                        .userName(wallets.get().getUser().getUserName())
                        .balance(wallets.get().getBalance())
                        .currency(wallets.get().getCurrency())
                        .build();
            } else{
                throw new Exception("Failed to get User");
            }
        } catch (Exception exception){
            log.error("Failed to get User", exception);
            throw exception;
        }
    }
}
