package com.assino.service;

import com.assino.dto.Dtos.AssinaturaRequest;
import com.assino.dto.Dtos.AssinaturaResponse;
import com.assino.exception.ResourceNotFoundException;
import com.assino.model.Assinatura;
import com.assino.model.CategoriaAssinatura;
import com.assino.model.ResumoGastos;
import com.assino.model.Usuario;
import com.assino.repository.AssinaturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Regras de negócio das assinaturas.
 */
@Service
@RequiredArgsConstructor
public class AssinaturaService {

    private final AssinaturaRepository assinaturaRepository;
    private final UsuarioService usuarioService;

    @Transactional
    public AssinaturaResponse criar(AssinaturaRequest req, String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        Assinatura a = new Assinatura();
        a.setUsuario(usuario);
        aplicar(a, req);
        return AssinaturaResponse.de(assinaturaRepository.save(a));
    }

    @Transactional
    public AssinaturaResponse atualizar(Long id, AssinaturaRequest req, String email) {
        Assinatura a = buscarDoUsuario(id, email);
        aplicar(a, req);
        return AssinaturaResponse.de(assinaturaRepository.save(a));
    }

    @Transactional
    public void deletar(Long id, String email) {
        assinaturaRepository.delete(buscarDoUsuario(id, email));
    }

    @Transactional(readOnly = true)
    public AssinaturaResponse buscar(Long id, String email) {
        return AssinaturaResponse.de(buscarDoUsuario(id, email));
    }

    @Transactional(readOnly = true)
    public List<AssinaturaResponse> listar(String email) {
        return converter(assinaturaRepository.findByUsuarioId(idDe(email)));
    }

    @Transactional(readOnly = true)
    public List<AssinaturaResponse> listarAtivas(String email) {
        return converter(assinaturaRepository.findByUsuarioIdAndAtivaTrue(idDe(email)));
    }

    @Transactional(readOnly = true)
    public List<AssinaturaResponse> listarPorCategoria(String email, CategoriaAssinatura categoria) {
        return converter(assinaturaRepository.findByUsuarioIdAndCategoria(idDe(email), categoria));
    }

    /** Calcula o total mensal, anual e por categoria das assinaturas ativas. */
    @Transactional(readOnly = true)
    public ResumoGastos resumo(String email) {
        List<Assinatura> ativas = assinaturaRepository.findByUsuarioIdAndAtivaTrue(idDe(email));
        BigDecimal totalMensal = BigDecimal.ZERO;
        Map<String, BigDecimal> porCategoria = new TreeMap<>();
        for (Assinatura a : ativas) {
            totalMensal = totalMensal.add(a.getValor());
            String cat = a.getCategoria() != null ? a.getCategoria().name() : CategoriaAssinatura.OUTROS.name();
            porCategoria.merge(cat, a.getValor(), BigDecimal::add);
        }
        return new ResumoGastos(totalMensal, totalMensal.multiply(BigDecimal.valueOf(12)),
                ativas.size(), porCategoria);
    }

    private void aplicar(Assinatura a, AssinaturaRequest req) {
        a.setNome(req.nome());
        a.setDescricao(req.descricao());
        a.setValor(req.valor());
        a.setDataCobranca(req.dataCobranca());
        a.setCategoria(req.categoria());
        a.setAtiva(req.ativa() == null || req.ativa());
    }

    /** Garante que a assinatura pertence ao usuário logado. */
    private Assinatura buscarDoUsuario(Long id, String email) {
        Long usuarioId = idDe(email);
        return assinaturaRepository.findById(id)
                .filter(a -> a.getUsuario().getId().equals(usuarioId))
                .orElseThrow(() -> new ResourceNotFoundException("Assinatura não encontrada"));
    }

    private Long idDe(String email) {
        return usuarioService.buscarPorEmail(email).getId();
    }

    private List<AssinaturaResponse> converter(List<Assinatura> lista) {
        return lista.stream().map(AssinaturaResponse::de).toList();
    }
}
