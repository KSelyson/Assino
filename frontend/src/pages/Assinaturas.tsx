import { useCallback, useEffect, useMemo, useState } from "react";
import { assinaturaApi, mensagemErro } from "../api/client";
import { CATEGORIAS, type Assinatura, type AssinaturaInput, type Categoria } from "../api/types";
import AssinaturaForm from "../components/AssinaturaForm";
import Modal from "../components/Modal";
import { moeda } from "../utils";

type Status = "TODAS" | "ATIVAS" | "INATIVAS";
type Ordem = "nome" | "valor" | "dia";

export default function Assinaturas() {
  const [lista, setLista] = useState<Assinatura[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [busca, setBusca] = useState("");
  const [categoria, setCategoria] = useState<Categoria | "">("");
  const [status, setStatus] = useState<Status>("TODAS");
  const [ordem, setOrdem] = useState<Ordem>("dia");
  const [modalAberto, setModalAberto] = useState(false);
  const [editando, setEditando] = useState<Assinatura | null>(null);

  const carregar = useCallback(async () => {
    try {
      setLista(await assinaturaApi.listar());
      setErro("");
    } catch (e) {
      setErro(mensagemErro(e));
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => { carregar(); }, [carregar]);

  const filtrada = useMemo(() => {
    const termo = busca.trim().toLowerCase();
    return lista
      .filter((a) => !termo || a.nome.toLowerCase().includes(termo) || a.descricao?.toLowerCase().includes(termo))
      .filter((a) => !categoria || a.categoria === categoria)
      .filter((a) => status === "TODAS" || (status === "ATIVAS" ? a.ativa : !a.ativa))
      .sort((a, b) =>
        ordem === "nome" ? a.nome.localeCompare(b.nome)
        : ordem === "valor" ? Number(b.valor) - Number(a.valor)
        : a.dataCobranca - b.dataCobranca);
  }, [lista, busca, categoria, status, ordem]);

  const totalFiltrado = filtrada.filter((a) => a.ativa).reduce((s, a) => s + Number(a.valor), 0);

  const abrirNova = () => { setEditando(null); setModalAberto(true); };
  const abrirEdicao = (a: Assinatura) => { setEditando(a); setModalAberto(true); };
  const fechar = useCallback(() => setModalAberto(false), []);

  const salvar = async (dados: AssinaturaInput) => {
    try {
      if (editando) await assinaturaApi.atualizar(editando.id, dados);
      else await assinaturaApi.criar(dados);
    } catch (e) {
      throw new Error(mensagemErro(e));
    }
    fechar();
    await carregar();
  };

  const alternarAtiva = async (a: Assinatura) => {
    try {
      const { id, dataCriacao, dataAtualizacao, ...dados } = a;
      void dataCriacao; void dataAtualizacao;
      await assinaturaApi.atualizar(id, { ...dados, ativa: !a.ativa });
      await carregar();
    } catch (e) {
      setErro(mensagemErro(e));
    }
  };

  const excluir = async (a: Assinatura) => {
    if (!confirm(`Excluir a assinatura "${a.nome}"?`)) return;
    try {
      await assinaturaApi.excluir(a.id);
      await carregar();
    } catch (e) {
      setErro(mensagemErro(e));
    }
  };

  return (
    <>
      <div className="cabecalho">
        <h1 className="titulo">Assinaturas</h1>
        <button className="btn" onClick={abrirNova}>+ Nova assinatura</button>
      </div>

      <div className="card filtros">
        <input placeholder="Buscar..." value={busca} onChange={(e) => setBusca(e.target.value)} />
        <select value={categoria} onChange={(e) => setCategoria(e.target.value as Categoria | "")}>
          <option value="">Todas as categorias</option>
          {Object.entries(CATEGORIAS).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
        </select>
        <select value={status} onChange={(e) => setStatus(e.target.value as Status)}>
          <option value="TODAS">Todos os status</option>
          <option value="ATIVAS">Ativas</option>
          <option value="INATIVAS">Inativas</option>
        </select>
        <select value={ordem} onChange={(e) => setOrdem(e.target.value as Ordem)}>
          <option value="dia">Ordenar por dia</option>
          <option value="valor">Ordenar por valor</option>
          <option value="nome">Ordenar por nome</option>
        </select>
      </div>

      {erro && <p className="erro">{erro}</p>}

      <div className="card">
        {carregando ? (
          <p className="muted">Carregando...</p>
        ) : filtrada.length === 0 ? (
          <p className="vazio">Nenhuma assinatura encontrada.</p>
        ) : (
          <>
            <table className="tabela">
              <thead>
                <tr><th>Nome</th><th>Categoria</th><th>Dia</th><th>Valor</th><th>Status</th><th /></tr>
              </thead>
              <tbody>
                {filtrada.map((a) => (
                  <tr key={a.id} className={a.ativa ? "" : "inativa"}>
                    <td>
                      <strong>{a.nome}</strong>
                      {a.descricao && <small className="muted bloco">{a.descricao}</small>}
                    </td>
                    <td><span className="tag">{CATEGORIAS[a.categoria]}</span></td>
                    <td>{a.dataCobranca}</td>
                    <td>{moeda(a.valor)}</td>
                    <td>
                      <button className={`status ${a.ativa ? "on" : "off"}`} onClick={() => alternarAtiva(a)} title="Alternar status">
                        {a.ativa ? "Ativa" : "Inativa"}
                      </button>
                    </td>
                    <td className="acoes">
                      <button className="btn-link" onClick={() => abrirEdicao(a)}>Editar</button>
                      <button className="btn-link perigo" onClick={() => excluir(a)}>Excluir</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <p className="total">Total mensal (ativas filtradas): <strong>{moeda(totalFiltrado)}</strong></p>
          </>
        )}
      </div>

      <Modal titulo={editando ? "Editar assinatura" : "Nova assinatura"} aberto={modalAberto} onFechar={fechar}>
        <AssinaturaForm key={editando?.id ?? "nova"} inicial={editando} onSalvar={salvar} onCancelar={fechar} />
      </Modal>
    </>
  );
}
