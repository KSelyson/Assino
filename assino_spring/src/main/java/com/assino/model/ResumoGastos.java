package com.assino.model;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Resumo dos gastos com assinaturas ativas do usuário.
 */
public record ResumoGastos(
        BigDecimal totalMensal,
        BigDecimal totalAnual,
        int quantidadeAssinaturas,
        Map<String, BigDecimal> gastosPorCategoria) {
}
