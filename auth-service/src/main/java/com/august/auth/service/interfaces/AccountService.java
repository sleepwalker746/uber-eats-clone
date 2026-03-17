package com.august.auth.service.interfaces;

import com.august.auth.dto.AccountResponseDTO;
import com.august.auth.dto.LoginRequestDTO;
import com.august.auth.dto.RegisterRequestDTO;

public interface AccountService {
    AccountResponseDTO register(RegisterRequestDTO registerRequestDTO);
    AccountResponseDTO login(LoginRequestDTO loginRequestDTO);
}
