package com.example.bankcards.service;

import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.NotEnoughBalanceException;
import com.example.bankcards.exception.NotSelfCardException;
import com.example.bankcards.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransitionService {
    private final CardRepository cardRepository;

    public void transferAmount(TransferDto transfer) {
        log.info("Identify card FROM");
        Card cardFrom = cardRepository.findByCardNumber(transfer.getCardFrom())
                .orElseThrow(() -> new CardNotFoundException("Card FROM not found"));

        log.info("Identify card TO");
        Card cardTo = cardRepository.findByCardNumber(transfer.getCardTo())
                .orElseThrow(() -> new CardNotFoundException("Card TO not found"));

        Long userId = transfer.getUserId();

        if (!cardFrom.getUser().getId().equals(userId) ||
                !cardTo.getUser().getId().equals(userId)) {
            throw new NotSelfCardException("You can transfer only between your own cards");
        }

        log.info("Start transfer...");
        Long oldBalance = cardFrom.getBalance();
        Long amountToTransfer = transfer.getAmount();

        if (oldBalance < amountToTransfer) {
            throw new NotEnoughBalanceException("Not enough balance");
        }

        cardFrom.setBalance(cardFrom.getBalance() - amountToTransfer);
        cardTo.setBalance(cardTo.getBalance() + amountToTransfer);

        cardRepository.save(cardFrom);
        cardRepository.save(cardTo);

        log.info("Transfer completed");
    }
}
