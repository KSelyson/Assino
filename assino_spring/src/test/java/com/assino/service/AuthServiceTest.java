package com.assino.service;

import com.assino.config.JwtTokenProvider;
import com.assino.dto.Dtos.CadastroRequest;
import com.assino.dto.Dtos.LoginRequest;
import com.assino.exception.BadRequestException;
import com.assino.model.Usuario;
import com.assino.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository repository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtTokenProvider jwt = new JwtTokenProvider("chave-de-teste-com-pelo-menos-32-caracteres-ok", 60_000);
    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(repository, encoder, jwt);
    }

    @Test
    void registrarNormalizaEmailGuardaHashEDevolveToken() {
        var resp = service.registrar(new CadastroRequest("Ana", "  Ana@Email.COM ", "segredo123"));

        verify(repository).save(argThat(u -> u.getEmail().equals("ana@email.com")
                && !u.getSenha().equals("segredo123")
                && encoder.matches("segredo123", u.getSenha())));
        assertThat(jwt.getEmailFromToken(resp.token())).isEqualTo("ana@email.com");
        assertThat(resp.tipo()).isEqualTo("Bearer");
    }

    @Test
    void registrarRecusaEmailDuplicado() {
        when(repository.existsByEmail("ana@email.com")).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(new CadastroRequest("Ana", "ana@email.com", "segredo123")))
                .isInstanceOf(BadRequestException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void loginComSenhaCertaEErrada() {
        Usuario ana = Usuario.builder().email("ana@email.com").senha(encoder.encode("segredo123")).build();
        when(repository.findByEmail("ana@email.com")).thenReturn(Optional.of(ana));

        assertThat(jwt.validateToken(service.login(new LoginRequest("ana@email.com", "segredo123")).token())).isTrue();
        assertThatThrownBy(() -> service.login(new LoginRequest("ana@email.com", "errada")))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void tokenAdulteradoEhInvalido() {
        String token = jwt.generateToken("ana@email.com");
        assertThat(jwt.validateToken(token + "x")).isFalse();
        assertThat(jwt.validateToken("lixo")).isFalse();
    }
}
