package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user-control")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDto user) {
        log.info("Start creating user");
        userService.createUser(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        log.info("Get user by id");
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/all/{page}/{pageSize}")
    public ResponseEntity<List<UserDto>> getAllUsers(@PathVariable int page,
            @PathVariable int pageSize) {
        log.info("Get all users");
        List<UserDto> Users = userService.getAllUsers(page, pageSize);
        return ResponseEntity.ok(Users);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(@Valid @RequestBody UserDto card) {
        log.info("Update user");
        userService.updateUser(card);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Delete user with id");
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
