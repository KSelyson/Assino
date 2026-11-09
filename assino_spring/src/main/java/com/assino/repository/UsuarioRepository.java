package com.assino.repository;

import com.assino.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade {@link Usuario}.
 * Fornece operações de persistência e consultas personalizadas para usuários.
 *
 * @author Assinô
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo endereço de e-mail.
     *
     * @param email endereço de e-mail do usuário
     * @return um {@link Optional} contendo o usuário encontrado, ou vazio se não existir
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se já existe um usuário cadastrado com o e-mail informado.
     *
     * @param email endereço de e-mail a ser verificado
     * @return {@code true} se o e-mail já estiver cadastrado, {@code false} caso contrário
     */
    boolean existsByEmail(String email);
}
