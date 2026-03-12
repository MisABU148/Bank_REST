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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @PreAuthorize("hasRole('ADMIN')")
    public void createCard(CardDto cardDto) {
        log.info("Save data to db");
        Card card = dtoToEntity(cardDto);
        cardRepository.save(card);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CardDto getCardById(Long id) {
        log.info("Get card by id: {}", id);
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id " + id));
        return entityToDto(card);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<CardDto> getAllCards(int page, int pageSize) {
        log.info("Get all cards");
        return cardRepository.findAll(PageRequest.of(page, pageSize)).stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateCard(CardDto cardDto) {
        log.info("Update card with id: {}", cardDto.getId());
        Card existingCard = cardRepository.findById(cardDto.getId())
                .orElseThrow(() -> new RuntimeException("Card not found with id " + cardDto.getId()));

        existingCard.setCardNumber(cardDto.getCardNumber());
        existingCard.setValidityPeriod(cardDto.getValidityPeriod());
        existingCard.setBalance(cardDto.getBalance());

        if (existingCard.getStatus() != null) {
            existingCard.setStatus(Status.fromString(cardDto.getStatus()));
        }

        if (cardDto.getUserId() != null) {
            User newUser = userRepository.findById(cardDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id " + cardDto.getUserId()));
            existingCard.setUser(newUser);
        }

        cardRepository.save(existingCard);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCard(Long id) {
        log.info("Delete card with id: {}", id);
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id " + id));
        cardRepository.delete(card);
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public void blockCard(BlockRequest request) {
        String userName = ((UserDetails) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal())).getUsername();

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Identify card");
        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new CardNotFoundException("Card not found"));

        if (!Objects.equals(card.getUser().getId(), user.getId())) {
            throw new NotSelfCardException("You can block only own card");
        }

        if (card.getStatus() != Status.ACTIVE) {
            throw new CardNotActiveException("Card can not be blocked");
        }

        log.info("Start blocking");
        card.setStatus(Status.BLOCKED);
    }

    @PreAuthorize("hasRole('USER')")
    public List<CardDto> getCardsByUserName(int page, int pageSize) {
        String userName = ((UserDetails) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal())).getUsername();

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long id = user.getId();
        log.info("Get cards for user with id {}", id);
        Page<Card> cards = id == null
                ? cardRepository.findAll(PageRequest.of(page, pageSize))
                : cardRepository.findByUserId(id, PageRequest.of(page, pageSize));

        return cards.stream()
                .map(this::entityToDto)
                .toList();
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public Long getBalance(@Valid BalanceRequest request) {
        String userName = ((UserDetails) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal())).getUsername();

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("See balance of car with number {}", request.getCardNumber());
        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new CardNotFoundException("Card not found"));

        if (!Objects.equals(card.getUser().getId(), user.getId())) {
            throw new NotSelfCardException("You can use only own card");
        }

        log.info("Get balance");
        return card.getBalance();
    }
}