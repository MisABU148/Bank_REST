package com.example.bankcards.service;

import com.example.bankcards.dto.BalanceRequest;
import com.example.bankcards.dto.BlockRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotActiveException;
import com.example.bankcards.exception.NotSelfCardException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(userDetails.getUsername()).thenReturn("testUser");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createCard_shouldSaveCard() {
        CardDto dto = new CardDto();
        dto.setCardNumber("1234567890123456");
        dto.setStatus("active");

        cardService.createCard(dto);

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository, times(1)).save(captor.capture());

        Card savedCard = captor.getValue();
        assertEquals("1234567890123456", savedCard.getCardNumber());
        assertEquals(Status.ACTIVE, savedCard.getStatus());
    }

    @Test
    void getCardById_shouldReturnCardDto() {
        Card card = new Card();
        card.setId(1L);
        card.setCardNumber("1234567890123456");
        card.setStatus(Status.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        CardDto result = cardService.getCardById(1L);
        assertNotNull(result);
        assertEquals("1234567890123456", result.getCardNumber());
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void updateCard_shouldUpdateExistingCard() {
        Card existing = new Card();
        existing.setId(1L);
        existing.setCardNumber("1111222233334444");
        existing.setStatus(Status.ACTIVE);

        CardDto dto = new CardDto();
        dto.setId(1L);
        dto.setCardNumber("9999888877776666");
        dto.setStatus("blocked");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(existing));

        cardService.updateCard(dto);

        assertEquals("9999888877776666", existing.getCardNumber());
        assertEquals(Status.BLOCKED, existing.getStatus());
        verify(cardRepository).save(existing);
    }

    @Test
    void deleteCard_shouldDeleteExistingCard() {
        Card existing = new Card();
        existing.setId(1L);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(existing));

        cardService.deleteCard(1L);

        verify(cardRepository).delete(existing);
    }

    @Test
    void blockCard_shouldSetStatusBlocked() {
        User user = new User();
        user.setId(1L);

        Card card = new Card();
        card.setUser(user);
        card.setStatus(Status.ACTIVE);
        card.setCardNumber("1234567890123456");

        BlockRequest request = new BlockRequest();
        request.setCardNumber("1234567890123456");

        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(cardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(card));

        cardService.blockCard(request);

        assertEquals(Status.BLOCKED, card.getStatus());
    }

    @Test
    void blockCard_shouldThrowIfNotOwnCard() {
        User user = new User();
        user.setId(1L);

        Card card = new Card();
        card.setUser(new User());
        card.getUser().setId(2L);
        card.setStatus(Status.ACTIVE);
        card.setCardNumber("1234567890123456");

        BlockRequest request = new BlockRequest();
        request.setCardNumber("1234567890123456");

        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(cardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(card));

        assertThrows(NotSelfCardException.class, () -> cardService.blockCard(request));
    }

    @Test
    void blockCard_shouldThrowIfNotActive() {
        User user = new User();
        user.setId(1L);

        Card card = new Card();
        card.setUser(user);
        card.setStatus(Status.BLOCKED);
        card.setCardNumber("1234567890123456");

        BlockRequest request = new BlockRequest();
        request.setCardNumber("1234567890123456");

        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(cardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(card));

        assertThrows(CardNotActiveException.class, () -> cardService.blockCard(request));
    }

    @Test
    void getBalance_shouldReturnBalance() {
        User user = new User();
        user.setId(1L);

        Card card = new Card();
        card.setUser(user);
        card.setBalance(1000L);
        card.setCardNumber("1234567890123456");

        BalanceRequest request = new BalanceRequest();
        request.setCardNumber("1234567890123456");

        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(cardRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(card));

        Long balance = cardService.getBalance(request);

        assertEquals(1000L, balance);
    }

    @Test
    void getCardsByUserName_shouldReturnListOfCardDto() {
        User user = new User();
        user.setId(1L);

        Card card = new Card();
        card.setUser(user);
        card.setCardNumber("1234567890123456");

        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(cardRepository.findByUserId(eq(1L), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(card)));

        List<CardDto> cards = cardService.getCardsByUserName(0, 10);

        assertEquals(1, cards.size());
        assertEquals("1234567890123456", cards.getFirst().getCardNumber());
    }
}