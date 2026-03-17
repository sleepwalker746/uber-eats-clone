package com.august.auth.service.implementation;

import com.august.auth.dto.AccountResponseDTO;
import com.august.auth.dto.LoginRequestDTO;
import com.august.auth.dto.RegisterRequestDTO;
import com.august.auth.entity.Account;
import com.august.auth.entity.Role;
import com.august.auth.mapper.AccountMapper;
import com.august.auth.repository.AccountRepository;
import com.august.auth.repository.RoleRepository;
import com.august.auth.security.JwtService;
import com.august.auth.service.interfaces.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AccountResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        if (!registerRequestDTO.getPassword().equals(registerRequestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords don't match");
        }
        if (accountRepository.existsByLogin(registerRequestDTO.getLogin())) {
            throw new IllegalArgumentException("Login already exists");
        }
        if (accountRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (accountRepository.existsByPhone(registerRequestDTO.getPhone())) {
            throw new IllegalArgumentException("Phone already exists");
        }

        Account account = accountMapper.toAccount(registerRequestDTO);
        account.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        account.setRoles(Set.of(userRole));
        Account savedAccount = accountRepository.save(account);
        String token = jwtService.generateToken(savedAccount);
        return accountMapper.toAccountResponseDTO(savedAccount, token);
    }

    @Override
    public AccountResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Account account = accountRepository.findByLogin(loginRequestDTO.getLogin())
                .orElseThrow(() -> new IllegalArgumentException("Invalid login or password!"));
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), account.getPassword())) {
            throw new IllegalArgumentException("Invalid login or password!");
        }
        String token = jwtService.generateToken(account);
        return accountMapper.toAccountResponseDTO(account, token);
    }
}
