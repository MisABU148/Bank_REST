package com.example.bankcards.controller;

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
@RequestMapping("/card")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CardController {
    @Autowired
    private final CardService cardService;

    @PostMapping("/create")
    public ResponseEntity<Void> createCard(@Valid @RequestBody CardDto card) {
        log.info("Start creating card");
        cardService.createCard(card);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCard(@PathVariable Long id) {
        log.info("Get card by id: {}", id);
        CardDto card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CardDto>> getAllCards() {
        log.info("Get all cards");
        List<CardDto> cards = cardService.getAllCards();
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateCard(@PathVariable Long id, @RequestBody CardDto card) {
        log.info("Update card with id: {}", id);
        cardService.updateCard(id, card);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        log.info("Delete card with id: {}", id);
        cardService.deleteCard(id);
        return ResponseEntity.ok().build();
    }
}
