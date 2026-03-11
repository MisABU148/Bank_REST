package com.example.bankcards.controller;

import com.example.bankcards.dto.BalanceRequest;
import com.example.bankcards.dto.BlockRequest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.security.CustomUserServiceImpl;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CardController.class)
@Import(SecurityTestConfig.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserServiceImpl customUserService;

    private ObjectMapper objectMapper;
    private CardDto testCard;
    private Date futureDate;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        futureDate = calendar.getTime();

        testCard = new CardDto();
        testCard.setId(1L);
        testCard.setCardNumber("1234567812345678");
        testCard.setValidityPeriod(futureDate);
        testCard.setStatus("active");
        testCard.setBalance(1000L);
        testCard.setUserId(1L);
    }

    @Test
    void createCard_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/admin/card-control/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCard)))
                .andExpect(status().isOk());

        Mockito.verify(cardService).createCard(any(CardDto.class));
    }

    @Test
    void createCard_withInvalidData_shouldReturnBadRequest() throws Exception {
        CardDto invalidCard = new CardDto();
        invalidCard.setCardNumber("123");
        invalidCard.setValidityPeriod(new Date());
        invalidCard.setStatus("invalid");
        invalidCard.setBalance(-100L);

        mockMvc.perform(post("/admin/card-control/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCard)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCardById_shouldReturnCard() throws Exception {
        Mockito.when(cardService.getCardById(1L)).thenReturn(testCard);

        mockMvc.perform(get("/admin/card-control/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cardNumber").value("1234567812345678"))
                .andExpect(jsonPath("$.status").value("active"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void getAllCards_shouldReturnList() throws Exception {
        List<CardDto> cards = List.of(testCard);
        Mockito.when(cardService.getAllCards(0, 10)).thenReturn(cards);

        mockMvc.perform(get("/admin/card-control/all/0/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cardNumber").value("1234567812345678"))
                .andExpect(jsonPath("$[0].status").value("active"))
                .andExpect(jsonPath("$[0].balance").value(1000))
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void updateCard_shouldReturnOk() throws Exception {
        CardDto updatedCard = new CardDto();
        updatedCard.setCardNumber("8765432187654321");
        updatedCard.setValidityPeriod(futureDate);
        updatedCard.setStatus("blocked");
        updatedCard.setBalance(2000L);
        updatedCard.setUserId(1L);

        mockMvc.perform(put("/admin/card-control/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCard)))
                .andExpect(status().isOk());

        Mockito.verify(cardService).updateCard(eq(1L), any(CardDto.class));
    }

    @Test
    void deleteCard_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/admin/card-control/delete/1"))
                .andExpect(status().isOk());

        Mockito.verify(cardService).deleteCard(1L);
    }

    @Test
    void blockCard_shouldReturnOk() throws Exception {
        BlockRequest blockRequest = new BlockRequest();
        blockRequest.setCardNumber("1234567812345678");

        mockMvc.perform(post("/user/card-control/block/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blockRequest)))
                .andExpect(status().isOk());

        Mockito.verify(cardService).blockCard(eq(1L), any(BlockRequest.class));
    }

    @Test
    void getBalance_shouldReturnBalance() throws Exception {
        BalanceRequest balanceRequest = new BalanceRequest();
        balanceRequest.setCardNumber("1234567812345678");

        Mockito.when(cardService.getBalance(1L, balanceRequest)).thenReturn(1000L);

        mockMvc.perform(post("/user/card-control/balance/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(balanceRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));
    }

    @Test
    void getCardsByUserId_shouldReturnList() throws Exception {
        List<CardDto> userCards = List.of(testCard);
        Mockito.when(cardService.getCardsByUserId(1L, 0, 10)).thenReturn(userCards);

        mockMvc.perform(get("/user/card-control/1/0/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].cardNumber").value("1234567812345678"));
    }

    @Test
    void getCardsByUserId_withPagination_shouldCallServiceWithCorrectParams() throws Exception {
        mockMvc.perform(get("/user/card-control/1/2/5"))
                .andExpect(status().isOk());

        Mockito.verify(cardService).getCardsByUserId(1L, 2, 5);
    }
}