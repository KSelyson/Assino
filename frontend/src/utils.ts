export const moeda = (v: number | undefined) =>
  Number(v ?? 0).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });

/** Dias até a próxima cobrança (considera meses mais curtos). */
export function diasAteCobranca(dia: number, hoje = new Date()): number {
  const ano = hoje.getFullYear();
  const mes = hoje.getMonth();
  const ultimoDia = (a: number, m: number) => new Date(a, m + 1, 0).getDate();
  let alvo = new Date(ano, mes, Math.min(dia, ultimoDia(ano, mes)));
  const hojeZero = new Date(ano, mes, hoje.getDate());
  if (alvo < hojeZero) alvo = new Date(ano, mes + 1, Math.min(dia, ultimoDia(ano, mes + 1)));
  return Math.round((alvo.getTime() - hojeZero.getTime()) / 86_400_000);
}
