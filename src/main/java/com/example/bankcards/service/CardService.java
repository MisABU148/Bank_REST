package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    private Card dtoToEntity(CardDto dto) {
        log.info("Map from dto to entity");
        Card card = new Card();
        card.setCardNumber(dto.getCardNumber());
        card.setValidityPeriod(dto.getValidityPeriod());
        card.setBalance(dto.getBalance());
        card.setStatus(Status.fromString(dto.getStatus()));

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id " + dto.getUserId()));
            card.setUser(user);
        }

        return card;
    }

    private CardDto entityToDto(Card card) {
        log.info("Map from entity to dto");
        CardDto dto = new CardDto();
        dto.setId(card.getId());
        dto.setCardNumber(card.getCardNumber());
        dto.setValidityPeriod(card.getValidityPeriod());
        dto.setBalance(card.getBalance());
        dto.setStatus(card.getStatus() != null ? card.getStatus().name() : null);
        dto.setUserId(card.getUser() != null ? card.getUser().getId() : null);
        return dto;
    }

    public void createCard(CardDto cardDto) {
        log.info("Save data to db");
        Card card = dtoToEntity(cardDto);
        cardRepository.save(card);
    }

    public CardDto getCardById(Long id) {
        log.info("Get card by id: {}", id);
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id " + id));
        return entityToDto(card);
    }

    public List<CardDto> getAllCards() {
        log.info("Get all cards");
        return cardRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public void updateCard(Long id, CardDto cardDto) {
        log.info("Update card with id: {}", id);
        Card existingCard = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id " + id));

        existingCard.setCardNumber(cardDto.getCardNumber());
        existingCard.setValidityPeriod(cardDto.getValidityPeriod());
        existingCard.setBalance(cardDto.getBalance());
        existingCard.setStatus(Status.valueOf(cardDto.getStatus()));

        if (cardDto.getUserId() != null) {
            User user = userRepository.findById(cardDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id " + cardDto.getUserId()));
            existingCard.setUser(user);
        }

        cardRepository.save(existingCard);
    }

    public void deleteCard(Long id) {
        log.info("Delete card with id: {}", id);
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id " + id));
        cardRepository.delete(card);
    }
}