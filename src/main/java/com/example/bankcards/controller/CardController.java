package com.example.bankcards.controller;

import com.example.bankcards.dto.BalanceRequest;
import com.example.bankcards.dto.BlockRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class CardController {
    @Autowired
    private final CardService cardService;

    @PostMapping("/admin/card-control/create")
    public ResponseEntity<Void> createCard(@Valid @RequestBody CardDto card) {
        log.info("Start creating card");
        cardService.createCard(card);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/card-control/{id}")
    public ResponseEntity<CardDto> getCardByCardId(@PathVariable Long id) {
        log.info("Get card by id: {}", id);
        CardDto card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/admin/card-control/all/{page}/{pageSize}")
    public ResponseEntity<List<CardDto>> getAllCards(@PathVariable int page, @PathVariable int pageSize) {
        log.info("Get all cards");
        List<CardDto> cards = cardService.getAllCards(page, pageSize);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/admin/card-control/update/{id}")
    public ResponseEntity<Void> updateCard(@PathVariable Long id, @Valid @RequestBody CardDto card) {
        log.info("Update card with id: {}", id);
        cardService.updateCard(id, card);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/card-control/delete/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        log.info("Delete card with id: {}", id);
        cardService.deleteCard(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/card-control/block/{id}")
    public ResponseEntity<Void> blockCard(@PathVariable Long id,
                                          @Valid @RequestBody BlockRequest request) {
        log.info("Start blocking card");
        cardService.blockCard(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/card-control/balance/{userId}")
    public ResponseEntity<Long> getBalanceCard(@PathVariable Long userId,
                                          @Valid @RequestBody BalanceRequest request) {
        log.info("Get balance for card");
        return ResponseEntity.ok(cardService.getBalance(userId, request));
    }

    @GetMapping("/user/card-control/{userId}/{page}/{pageSize}")
    public List<CardDto> getCardByUserId(@PathVariable Long userId,
                                         @PathVariable int page,
                                         @PathVariable int pageSize) {
        log.info("Get all card for user");
        return cardService.getCardsByUserId(userId, page, pageSize);
    }
}
