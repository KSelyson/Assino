package com.assino.repository;

import com.assino.model.Assinatura;
import com.assino.model.CategoriaAssinatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório JPA para a entidade {@link Assinatura}.
 * Fornece operações de persistência e consultas personalizadas para assinaturas.
 *
 * @author Assinô
 */
@Repository
public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {

    /**
     * Busca todas as assinaturas de um determinado usuário.
     *
     * @param usuarioId identificador do usuário
     * @return lista de assinaturas do usuário
     */
    List<Assinatura> findByUsuarioId(Long usuarioId);

    /**
     * Busca apenas as assinaturas ativas de um determinado usuário.
     *
     * @param usuarioId identificador do usuário
     * @return lista de assinaturas ativas do usuário
     */
    List<Assinatura> findByUsuarioIdAndAtivaTrue(Long usuarioId);

    /**
     * Busca as assinaturas de um usuário filtradas por categoria.
     *
     * @param usuarioId identificador do usuário
     * @param categoria categoria da assinatura
     * @return lista de assinaturas do usuário na categoria informada
     */
    List<Assinatura> findByUsuarioIdAndCategoria(Long usuarioId, CategoriaAssinatura categoria);
}
