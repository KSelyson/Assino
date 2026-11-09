package com.assino.service;

import com.assino.config.JwtTokenProvider;
import com.assino.dto.Dtos.CadastroRequest;
import com.assino.dto.Dtos.LoginRequest;
import com.assino.dto.Dtos.TokenResponse;
import com.assino.exception.BadRequestException;
import com.assino.model.Usuario;
import com.assino.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cadastro e autenticação de usuários.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse registrar(CadastroRequest req) {
        String email = req.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new BadRequestException("E-mail já cadastrado");
        }
        Usuario usuario = Usuario.builder()
                .nome(req.nome())
                .email(email)
                .senha(passwordEncoder.encode(req.senha()))
                .build();
        usuarioRepository.save(usuario);
        return new TokenResponse(jwtTokenProvider.generateToken(email));
    }

    public TokenResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(req.senha(), u.getSenha()))
                .orElseThrow(() -> new BadRequestException("E-mail ou senha inválidos"));
        return new TokenResponse(jwtTokenProvider.generateToken(usuario.getEmail()));
    }
}
