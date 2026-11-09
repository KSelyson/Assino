package com.assino.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Assinatura recorrente de um usuário (ex.: Netflix, Spotify).
 */
@Entity
@Table(name = "assinaturas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    @Size(max = 255)
    private String descricao;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    /** Dia do mês em que ocorre a cobrança. */
    @Min(1)
    @Max(31)
    @Column(name = "data_cobranca")
    private Integer dataCobranca;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private CategoriaAssinatura categoria;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativa = true;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist
    void prePersist() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = dataCriacao;
        if (ativa == null) ativa = true;
    }

    @PreUpdate
    void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
