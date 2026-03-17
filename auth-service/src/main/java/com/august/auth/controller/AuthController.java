package com.august.auth.controller;

import com.august.auth.dto.AccountResponseDTO;
import com.august.auth.dto.LoginRequestDTO;
import com.august.auth.dto.RegisterRequestDTO;
import com.august.auth.service.interfaces.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<AccountResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        log.info("Register request received for login: {}", registerRequestDTO.getLogin());
        AccountResponseDTO accountResponseDTO = accountService.register(registerRequestDTO);
        return ResponseEntity.ok(accountResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<AccountResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        log.info("Login attempt for user: {}", loginRequestDTO.getLogin());
        return ResponseEntity.ok(accountService.login(loginRequestDTO));
    }
}
