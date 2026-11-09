package com.assino.dto;

import com.assino.model.Assinatura;
import com.assino.model.CategoriaAssinatura;
import com.assino.model.Usuario;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTOs (records) usados na entrada e saída da API.
 */
public final class Dtos {

    private Dtos() {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String senha) {
    }

    public record CadastroRequest(
            @NotBlank @Size(max = 100) String nome,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String senha) {
    }

    public record TokenResponse(String token, String tipo) {
        public TokenResponse(String token) {
            this(token, "Bearer");
        }
    }

    public record UsuarioResponse(Long id, String nome, String email, LocalDateTime dataCriacao) {
        public static UsuarioResponse de(Usuario u) {
            return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getDataCriacao());
        }
    }

    public record AssinaturaRequest(
            @NotBlank @Size(max = 100) String nome,
            @Size(max = 255) String descricao,
            @NotNull @Positive BigDecimal valor,
            @NotNull @Min(1) @Max(31) Integer dataCobranca,
            @NotNull CategoriaAssinatura categoria,
            Boolean ativa) {
    }

    public record AssinaturaResponse(Long id, String nome, String descricao, BigDecimal valor,
                                     Integer dataCobranca, CategoriaAssinatura categoria, Boolean ativa,
                                     LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        public static AssinaturaResponse de(Assinatura a) {
            return new AssinaturaResponse(a.getId(), a.getNome(), a.getDescricao(), a.getValor(),
                    a.getDataCobranca(), a.getCategoria(), a.getAtiva(),
                    a.getDataCriacao(), a.getDataAtualizacao());
        }
    }
}
