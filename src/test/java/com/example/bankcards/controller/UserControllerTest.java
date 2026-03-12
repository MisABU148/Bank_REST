package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.security.CustomUserServiceImpl;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityTestConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserServiceImpl customUserService;

    private ObjectMapper objectMapper;
    private UserDto sampleUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -10);
        Date pastDate = calendar.getTime();

        sampleUser = new UserDto();
        sampleUser.setId(1L);
        sampleUser.setUserName("user1");
        sampleUser.setAge(pastDate);
        sampleUser.setRole("admin");
        sampleUser.setPassword("password");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/admin/user-control/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isOk());
        Mockito.verify(userService).createUser(any(UserDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUser_shouldReturnUser() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(sampleUser);

        mockMvc.perform(get("/admin/user-control/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("user1"))
                .andExpect(jsonPath("$.role").value("admin"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturnList() throws Exception {
        Mockito.when(userService.getAllUsers(0, 10)).thenReturn(List.of(sampleUser));

        mockMvc.perform(get("/admin/user-control/all/0/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userName").value("user1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/admin/user-control/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isOk());
        Mockito.verify(userService).updateUser(any(UserDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/admin/user-control/delete/1"))
                .andExpect(status().isOk());
        Mockito.verify(userService).deleteUser(anyLong());
    }
}