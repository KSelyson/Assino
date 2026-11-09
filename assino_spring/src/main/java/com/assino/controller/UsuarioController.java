package com.assino.controller;

import com.assino.dto.Dtos.UsuarioResponse;
import com.assino.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Dados do usuário autenticado.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/perfil")
    public UsuarioResponse perfil(Principal p) {
        return UsuarioResponse.de(usuarioService.buscarPorEmail(p.getName()));
    }
}
