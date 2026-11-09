package com.assino.model;

/**
 * Categorias de assinatura.
 */
public enum CategoriaAssinatura {
    STREAMING("Streaming"),
    MUSICA("Música"),
    JOGOS("Jogos"),
    CLOUD("Armazenamento em nuvem"),
    SOFTWARE("Software"),
    EDUCACAO("Educação"),
    ALIMENTACAO("Alimentação"),
    SAUDE("Saúde"),
    TRANSPORTE("Transporte"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaAssinatura(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
