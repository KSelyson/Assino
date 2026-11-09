export type Categoria =
  | "STREAMING" | "MUSICA" | "JOGOS" | "CLOUD" | "SOFTWARE"
  | "EDUCACAO" | "ALIMENTACAO" | "SAUDE" | "TRANSPORTE" | "OUTROS";

export const CATEGORIAS: Record<Categoria, string> = {
  STREAMING: "Streaming",
  MUSICA: "Música",
  JOGOS: "Jogos",
  CLOUD: "Nuvem",
  SOFTWARE: "Software",
  EDUCACAO: "Educação",
  ALIMENTACAO: "Alimentação",
  SAUDE: "Saúde",
  TRANSPORTE: "Transporte",
  OUTROS: "Outros",
};

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  dataCriacao: string;
}

export interface Assinatura {
  id: number;
  nome: string;
  descricao: string | null;
  valor: number;
  dataCobranca: number;
  categoria: Categoria;
  ativa: boolean;
  dataCriacao: string;
  dataAtualizacao: string;
}

export type AssinaturaInput = Omit<Assinatura, "id" | "dataCriacao" | "dataAtualizacao">;

export interface ResumoGastos {
  totalMensal: number;
  totalAnual: number;
  quantidadeAssinaturas: number;
  gastosPorCategoria: Partial<Record<Categoria, number>>;
}

export interface ErroApi {
  status: number;
  mensagem: string;
  campos?: Record<string, string>;
}
