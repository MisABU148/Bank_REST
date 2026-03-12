package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.security.CustomUserServiceImpl;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.TransitionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransitionController.class)
@Import(SecurityTestConfig.class)
public class TransitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransitionService transitionService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserServiceImpl customUserService;

    private ObjectMapper objectMapper;
    private TransferDto testTransfer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        testTransfer = new TransferDto();
        testTransfer.setCardFrom("1234567812345678");
        testTransfer.setCardTo("8765432187654321");
        testTransfer.setAmount(500L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void ownTransfer_shouldReturnOk_forUser() throws Exception {
        mockMvc.perform(post("/user/card-control/transition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTransfer)))
                .andExpect(status().isOk());

        Mockito.verify(transitionService).transferAmount(any(TransferDto.class));
    }
}
