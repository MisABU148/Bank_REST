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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
    @WithMockUser(roles = "ADMIN")
    void createCard_shouldReturnOk_forAdmin() throws Exception {
        mockMvc.perform(post("/admin/card-control/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCard)))
                .andExpect(status().isOk());

        Mockito.verify(cardService).createCard(any(CardDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCardById_shouldReturnCard_forAdmin() throws Exception {
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
    @WithMockUser(roles = "ADMIN")
    void getAllCards_shouldReturnList_forAdmin() throws Exception {
        List<CardDto> cards = List.of(testCard);
        Mockito.when(cardService.getAllCards(0, 10)).thenReturn(cards);

        mockMvc.perform(get("/admin/card-control/all/0/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cardNumber").value("1234567812345678"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCard_shouldReturnOk_forAdmin() throws Exception {
        CardDto updatedCard = new CardDto();
        updatedCard.setId(1L);
        updatedCard.setCardNumber("8765432187654321");
        updatedCard.setValidityPeriod(futureDate);
        updatedCard.setStatus("blocked");
        updatedCard.setBalance(2000L);
        updatedCard.setUserId(1L);

        mockMvc.perform(put("/admin/card-control/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCard)))
                .andExpect(status().isOk());

        Mockito.verify(cardService).updateCard(any(CardDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCard_shouldReturnOk_forAdmin() throws Exception {
        mockMvc.perform(delete("/admin/card-control/delete/1"))
                .andExpect(status().isOk());

        Mockito.verify(cardService).deleteCard(1L);
    }
    @Test
    @WithMockUser(roles = "USER")
    void blockCard_shouldReturnOk_forUser() throws Exception {
        BlockRequest request = new BlockRequest();
        request.setCardNumber("1234567812345678");

        mockMvc.perform(post("/user/card-control/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(cardService).blockCard(any(BlockRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getBalance_shouldReturnBalance_forUser() throws Exception {
        BalanceRequest request = new BalanceRequest();
        request.setCardNumber("1234567812345678");

        Mockito.when(cardService.getBalance(any(BalanceRequest.class))).thenReturn(1000L);

        mockMvc.perform(post("/user/card-control/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCardsByUser_shouldReturnList_forUser() throws Exception {
        List<CardDto> userCards = List.of(testCard);
        Mockito.when(cardService.getCardsByUserName(0, 10)).thenReturn(userCards);

        mockMvc.perform(get("/user/card-control/0/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cardNumber").value("1234567812345678"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminEndpoints_shouldReturnForbidden_forUser() throws Exception {
        mockMvc.perform(post("/admin/card-control/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCard)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/admin/card-control/1"))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/admin/card-control/delete/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void userEndpoints_shouldReturnForbidden_forAdmin() throws Exception {
        BlockRequest request = new BlockRequest();
        request.setCardNumber("1234567812345678");

        mockMvc.perform(post("/user/card-control/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/user/card-control/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BalanceRequest())))
                .andExpect(status().isForbidden());
    }
}