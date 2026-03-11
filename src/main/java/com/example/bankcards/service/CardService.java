package com.example.bankcards.service;

import com.example.bankcards.dto.BalanceRequest;
import com.example.bankcards.dto.BlockRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotActiveException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.NotSelfCardException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;
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

    public List<CardDto> getAllCards(int page, int pageSize) {
        log.info("Get all cards");
        return cardRepository.findAll(PageRequest.of(page, pageSize)).stream()
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

        if (existingCard.getStatus() != null) {
            existingCard.setStatus(Status.fromString(cardDto.getStatus()));
        }

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

    @Transactional
    public void blockCard(Long id, BlockRequest request) {
        log.info("Identify card");
        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new CardNotFoundException("Card not found"));

        if (!Objects.equals(card.getUser().getId(), id)) {
            throw new NotSelfCardException("You can block only own card");
        }

        if (card.getStatus() != Status.ACTIVE) {
            throw new CardNotActiveException("Card can not be blocked");
        }

        log.info("Start blocking");
        card.setStatus(Status.BLOCKED);
    }

    public List<CardDto> getCardsByUserId(Long id, int page, int pageSize) {
        log.info("Get cards for user with id {}", id);
        Page<Card> cards = id == null
                ? cardRepository.findAll(PageRequest.of(page, pageSize))
                : cardRepository.findByUserId(id, PageRequest.of(page, pageSize));

        return cards.stream()
                .map(this::entityToDto)
                .toList();
    }

    @Transactional
    public Long getBalance(Long userId, @Valid BalanceRequest request) {
        log.info("See balance of car with number {}", request.getCardNumber());
        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new CardNotFoundException("Card not found"));

        if (!Objects.equals(card.getUser().getId(), userId)) {
            throw new NotSelfCardException("You can use only own card");
        }

        log.info("Get balance");
        return card.getBalance();
    }
}