package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.service.TransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/card-control/transition")
@RequiredArgsConstructor
@Slf4j
public class TransitionController {
    private final TransitionService transitionService;

    @PostMapping
    public ResponseEntity<Void> ownTransfer(@RequestBody TransferDto transfer) {
        log.info("Attempt to transfer btw user`s cards");
        transitionService.transferAmount(transfer);
        return ResponseEntity.ok().build();
    }
}
