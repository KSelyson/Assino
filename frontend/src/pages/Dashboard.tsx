import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts";
import { assinaturaApi, mensagemErro } from "../api/client";
import { CATEGORIAS, type Assinatura, type Categoria, type ResumoGastos } from "../api/types";
import { diasAteCobranca, moeda } from "../utils";

const CORES = ["#7c3aed", "#06b6d4", "#f59e0b", "#10b981", "#ef4444", "#3b82f6", "#ec4899", "#84cc16", "#f97316", "#6b7280"];

export default function Dashboard() {
  const [resumo, setResumo] = useState<ResumoGastos | null>(null);
  const [lista, setLista] = useState<Assinatura[]>([]);
  const [erro, setErro] = useState("");

  useEffect(() => {
    Promise.all([assinaturaApi.resumo(), assinaturaApi.listar()])
      .then(([r, l]) => { setResumo(r); setLista(l); })
      .catch((e) => setErro(mensagemErro(e)));
  }, []);

  if (erro) return <p className="erro">{erro}</p>;
  if (!resumo) return <p className="muted">Carregando...</p>;

  const dadosGrafico = Object.entries(resumo.gastosPorCategoria).map(([cat, valor]) => ({
    nome: CATEGORIAS[cat as Categoria] ?? cat,
    valor: Number(valor),
  }));

  const proximas = lista
    .filter((a) => a.ativa)
    .map((a) => ({ ...a, dias: diasAteCobranca(a.dataCobranca) }))
    .sort((a, b) => a.dias - b.dias)
    .slice(0, 5);

  return (
    <>
      <h1 className="titulo">Painel</h1>
      <div className="stats">
        <div className="card stat"><span>Gasto mensal</span><strong>{moeda(resumo.totalMensal)}</strong></div>
        <div className="card stat"><span>Projeção anual</span><strong>{moeda(resumo.totalAnual)}</strong></div>
        <div className="card stat"><span>Assinaturas ativas</span><strong>{resumo.quantidadeAssinaturas}</strong></div>
      </div>

      <div className="grid-2">
        <div className="card">
          <h2>Gastos por categoria</h2>
          {dadosGrafico.length === 0 ? (
            <p className="vazio">Nenhuma assinatura ativa. <Link to="/assinaturas">Cadastre a primeira</Link>.</p>
          ) : (
            <div className="grafico">
              <ResponsiveContainer width="100%" height={240}>
                <PieChart>
                  <Pie data={dadosGrafico} dataKey="valor" nameKey="nome" innerRadius={55} outerRadius={95} paddingAngle={2}>
                    {dadosGrafico.map((_, i) => <Cell key={i} fill={CORES[i % CORES.length]} />)}
                  </Pie>
                  <Tooltip formatter={(v) => moeda(Number(v))} />
                </PieChart>
              </ResponsiveContainer>
              <ul className="legenda">
                {dadosGrafico.map((d, i) => (
                  <li key={d.nome}>
                    <span className="bolinha" style={{ background: CORES[i % CORES.length] }} />
                    {d.nome}<strong>{moeda(d.valor)}</strong>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>

        <div className="card">
          <h2>Próximas cobranças</h2>
          {proximas.length === 0 ? (
            <p className="vazio">Nada por aqui.</p>
          ) : (
            <ul className="lista-simples">
              {proximas.map((a) => (
                <li key={a.id}>
                  <div>
                    <strong>{a.nome}</strong>
                    <small className="muted">
                      Dia {a.dataCobranca} • {a.dias === 0 ? "hoje" : a.dias === 1 ? "amanhã" : `em ${a.dias} dias`}
                    </small>
                  </div>
                  <strong>{moeda(a.valor)}</strong>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </>
  );
}
