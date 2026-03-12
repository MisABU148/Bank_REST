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

    @GetMapping("/admin/card-control/{cardId}")
    public ResponseEntity<CardDto> getCardByCardId(@PathVariable Long cardId)  {
        log.info("Get card by username");
        CardDto card = cardService.getCardById(cardId);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/admin/card-control/all/{page}/{pageSize}")
    public ResponseEntity<List<CardDto>> getAllCards(@PathVariable int page,
                                                     @PathVariable int pageSize) {
        log.info("Get all cards");
        List<CardDto> cards = cardService.getAllCards(page, pageSize);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/admin/card-control/update/")
    public ResponseEntity<Void> updateCard(@Valid @RequestBody CardDto card) {
        log.info("Update card");
        cardService.updateCard(card);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/card-control/delete/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        log.info("Delete card with id: {}", id);
        cardService.deleteCard(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/card-control/block")
    public ResponseEntity<Void> blockCard(@Valid @RequestBody BlockRequest request) {
        log.info("Start blocking card");
        cardService.blockCard(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/card-control/balance")
    public ResponseEntity<Long> getBalanceCard(@Valid @RequestBody BalanceRequest request) {
        log.info("Get balance for card");
        return ResponseEntity.ok(cardService.getBalance(request));
    }

    @GetMapping("/user/card-control/{page}/{pageSize}")
    public List<CardDto> getCardByUserId(@PathVariable int page,
                                         @PathVariable int pageSize)  {
        log.info("Get all card for user");
        return cardService.getCardsByUserName(page, pageSize);
    }
}
