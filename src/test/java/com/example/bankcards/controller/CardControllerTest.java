package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private JwtService jwtService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanup() throws Exception {
        objectMapper = new ObjectMapper();
    }

    @Test
    void createCard_shouldReturnOk() throws Exception {

        CardDto card = new CardDto();
        card.setCardNumber("1234567812345678");
        card.setBalance(1000L);

        mockMvc.perform(post("/admin/card-control/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(card)))
                .andExpect(status().isOk());

        Mockito.verify(cardService).createCard(Mockito.any());
    }

    @Test
    void getCardById_shouldReturnCard() throws Exception {

        CardDto card = new CardDto();
        card.setId(1L);
        card.setCardNumber("1234567812345678");

        Mockito.when(cardService.getCardById(1L)).thenReturn(card);

        mockMvc.perform(get("/admin/card-control/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAllCards_shouldReturnList() throws Exception {

        CardDto card = new CardDto();
        card.setId(1L);

        Mockito.when(cardService.getAllCards(0,10))
                .thenReturn(List.of(card));

        mockMvc.perform(get("/admin/card-control/all/0/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}