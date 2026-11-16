package com.assino.service;

import com.assino.dto.Dtos.AssinaturaRequest;
import com.assino.exception.ResourceNotFoundException;
import com.assino.model.Assinatura;
import com.assino.model.CategoriaAssinatura;
import com.assino.model.ResumoGastos;
import com.assino.model.Usuario;
import com.assino.repository.AssinaturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssinaturaServiceTest {

    private static final String EMAIL = "ana@email.com";

    @Mock
    private AssinaturaRepository repository;
    @Mock
    private UsuarioService usuarioService;
    @InjectMocks
    private AssinaturaService service;

    private Usuario ana;

    @BeforeEach
    void setUp() {
        ana = Usuario.builder().id(1L).nome("Ana").email(EMAIL).build();
        lenient().when(usuarioService.buscarPorEmail(EMAIL)).thenReturn(ana);
    }

    private Assinatura assinatura(Long id, Usuario dono, String valor, CategoriaAssinatura cat) {
        return Assinatura.builder().id(id).nome("A" + id).valor(new BigDecimal(valor))
                .dataCobranca(10).categoria(cat).ativa(true).usuario(dono).build();
    }

    @Test
    void resumoSomaTotalMensalAnualEPorCategoria() {
        when(repository.findByUsuarioIdAndAtivaTrue(1L)).thenReturn(List.of(
                assinatura(1L, ana, "39.90", CategoriaAssinatura.STREAMING),
                assinatura(2L, ana, "20.10", CategoriaAssinatura.STREAMING),
                assinatura(3L, ana, "21.90", CategoriaAssinatura.MUSICA),
                assinatura(4L, ana, "5.00", null)));

        ResumoGastos r = service.resumo(EMAIL);

        assertThat(r.totalMensal()).isEqualByComparingTo("86.90");
        assertThat(r.totalAnual()).isEqualByComparingTo("1042.80");
        assertThat(r.quantidadeAssinaturas()).isEqualTo(4);
        assertThat(r.gastosPorCategoria().get("STREAMING")).isEqualByComparingTo("60.00");
        assertThat(r.gastosPorCategoria().get("MUSICA")).isEqualByComparingTo("21.90");
        assertThat(r.gastosPorCategoria().get("OUTROS")).isEqualByComparingTo("5.00");
    }

    @Test
    void resumoSemAssinaturasEhZero() {
        when(repository.findByUsuarioIdAndAtivaTrue(1L)).thenReturn(List.of());

        ResumoGastos r = service.resumo(EMAIL);

        assertThat(r.totalMensal()).isEqualByComparingTo("0");
        assertThat(r.quantidadeAssinaturas()).isZero();
        assertThat(r.gastosPorCategoria()).isEmpty();
    }

    @Test
    void criarAssociaAoUsuarioEAtivaPorPadrao() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var req = new AssinaturaRequest("Netflix", null, new BigDecimal("39.90"), 5,
                CategoriaAssinatura.STREAMING, null);

        var resp = service.criar(req, EMAIL);

        assertThat(resp.nome()).isEqualTo("Netflix");
        assertThat(resp.ativa()).isTrue();
        verify(repository).save(argThat(a -> a.getUsuario() == ana));
    }

    @Test
    void naoPermiteAcessarAssinaturaDeOutroUsuario() {
        Usuario bruno = Usuario.builder().id(2L).email("bruno@email.com").build();
        when(repository.findById(9L)).thenReturn(Optional.of(assinatura(9L, bruno, "10", CategoriaAssinatura.JOGOS)));

        assertThatThrownBy(() -> service.buscar(9L, EMAIL)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> service.deletar(9L, EMAIL)).isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).delete(any());
    }

    @Test
    void atualizarAlteraCamposEPodeDesativar() {
        Assinatura existente = assinatura(3L, ana, "10", CategoriaAssinatura.JOGOS);
        when(repository.findById(3L)).thenReturn(Optional.of(existente));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var req = new AssinaturaRequest("Game Pass", "Ultimate", new BigDecimal("59.99"), 20,
                CategoriaAssinatura.JOGOS, false);

        var resp = service.atualizar(3L, req, EMAIL);

        assertThat(resp.nome()).isEqualTo("Game Pass");
        assertThat(resp.valor()).isEqualByComparingTo("59.99");
        assertThat(resp.dataCobranca()).isEqualTo(20);
        assertThat(resp.ativa()).isFalse();
    }
}
