package com.ms.assessment.controller;

import com.ms.assessment.constants.ResourcePath;
import com.ms.assessment.model.BalanceResponseDTO;
import com.ms.assessment.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ResourcePath.WALLETS)
public class WalletController {

    @Autowired
    WalletService walletService;

    @GetMapping(ResourcePath.GET_BALANCE_API_URL)
    public ResponseEntity<?> getBalanceByOwner(@PathVariable String owner) throws Exception {
        try {
            BalanceResponseDTO balanceResponseDTO = walletService.getBalanceByOwner(owner);
            return ResponseEntity.ok(balanceResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
