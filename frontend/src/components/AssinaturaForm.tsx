import { useState, type FormEvent } from "react";
import { CATEGORIAS, type Assinatura, type AssinaturaInput, type Categoria } from "../api/types";

interface Props {
  inicial?: Assinatura | null;
  onSalvar: (dados: AssinaturaInput) => Promise<void>;
  onCancelar: () => void;
}

export default function AssinaturaForm({ inicial, onSalvar, onCancelar }: Props) {
  const [form, setForm] = useState({
    nome: inicial?.nome ?? "",
    descricao: inicial?.descricao ?? "",
    valor: inicial?.valor?.toString() ?? "",
    dataCobranca: inicial?.dataCobranca?.toString() ?? "",
    categoria: (inicial?.categoria ?? "STREAMING") as Categoria,
    ativa: inicial?.ativa ?? true,
  });
  const [erro, setErro] = useState("");
  const [salvando, setSalvando] = useState(false);

  const set = <K extends keyof typeof form>(k: K, v: (typeof form)[K]) => setForm((f) => ({ ...f, [k]: v }));

  const enviar = async (e: FormEvent) => {
    e.preventDefault();
    setErro("");
    const valor = Number(form.valor.replace(",", "."));
    const dia = Number(form.dataCobranca);
    if (!form.nome.trim()) return setErro("Informe o nome.");
    if (!(valor > 0)) return setErro("O valor deve ser maior que zero.");
    if (!(dia >= 1 && dia <= 31)) return setErro("O dia de cobrança deve estar entre 1 e 31.");

    setSalvando(true);
    try {
      await onSalvar({
        nome: form.nome.trim(),
        descricao: form.descricao.trim() || null,
        valor,
        dataCobranca: dia,
        categoria: form.categoria,
        ativa: form.ativa,
      });
    } catch (err) {
      setErro((err as Error).message);
    } finally {
      setSalvando(false);
    }
  };

  return (
    <form onSubmit={enviar} className="form">
      <label>
        Nome
        <input value={form.nome} onChange={(e) => set("nome", e.target.value)} maxLength={100} placeholder="Ex.: Netflix" autoFocus />
      </label>
      <label>
        Descrição
        <input value={form.descricao} onChange={(e) => set("descricao", e.target.value)} maxLength={255} placeholder="Opcional" />
      </label>
      <div className="form-linha">
        <label>
          Valor mensal (R$)
          <input inputMode="decimal" value={form.valor} onChange={(e) => set("valor", e.target.value)} placeholder="39,90" />
        </label>
        <label>
          Dia da cobrança
          <input type="number" min={1} max={31} value={form.dataCobranca} onChange={(e) => set("dataCobranca", e.target.value)} placeholder="10" />
        </label>
      </div>
      <label>
        Categoria
        <select value={form.categoria} onChange={(e) => set("categoria", e.target.value as Categoria)}>
          {Object.entries(CATEGORIAS).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
        </select>
      </label>
      <label className="check">
        <input type="checkbox" checked={form.ativa} onChange={(e) => set("ativa", e.target.checked)} />
        Assinatura ativa
      </label>
      {erro && <p className="erro">{erro}</p>}
      <div className="form-acoes">
        <button type="button" className="btn btn-sec" onClick={onCancelar}>Cancelar</button>
        <button type="submit" className="btn" disabled={salvando}>{salvando ? "Salvando..." : "Salvar"}</button>
      </div>
    </form>
  );
}
