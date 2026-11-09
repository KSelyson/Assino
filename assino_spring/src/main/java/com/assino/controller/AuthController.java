package com.assino.controller;

import com.assino.dto.Dtos.CadastroRequest;
import com.assino.dto.Dtos.LoginRequest;
import com.assino.dto.Dtos.TokenResponse;
import com.assino.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints públicos de cadastro e login.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse registrar(@Valid @RequestBody CadastroRequest req) {
        return authService.registrar(req);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}
