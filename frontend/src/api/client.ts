import axios, { AxiosError } from "axios";
import type { Assinatura, AssinaturaInput, ErroApi, ResumoGastos, Usuario } from "./types";

const TOKEN_KEY = "assino_token";

export const tokenStorage = {
  get: () => localStorage.getItem(TOKEN_KEY),
  set: (t: string) => localStorage.setItem(TOKEN_KEY, t),
  clear: () => localStorage.removeItem(TOKEN_KEY),
};

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? "/api",
});

// Anexa o token JWT em todas as requisições
http.interceptors.request.use((config) => {
  const token = tokenStorage.get();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Em caso de 401, limpa a sessão e avisa a aplicação
http.interceptors.response.use(
  (r) => r,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      tokenStorage.clear();
      window.dispatchEvent(new Event("assino:logout"));
    }
    return Promise.reject(error);
  }
);

/** Converte erros da API em uma mensagem legível. */
export function mensagemErro(e: unknown): string {
  const err = e as AxiosError<ErroApi>;
  const data = err.response?.data;
  if (data?.campos) {
    return Object.entries(data.campos).map(([c, m]) => `${c}: ${m}`).join(" • ");
  }
  if (data?.mensagem) return data.mensagem;
  if (!err.response) return "Não foi possível conectar ao servidor.";
  return "Erro inesperado.";
}

export const authApi = {
  login: (email: string, senha: string) =>
    http.post<{ token: string }>("/auth/login", { email, senha }).then((r) => r.data),
  registrar: (nome: string, email: string, senha: string) =>
    http.post<{ token: string }>("/auth/registrar", { nome, email, senha }).then((r) => r.data),
  perfil: () => http.get<Usuario>("/usuarios/perfil").then((r) => r.data),
};

export const assinaturaApi = {
  listar: () => http.get<Assinatura[]>("/assinaturas").then((r) => r.data),
  resumo: () => http.get<ResumoGastos>("/assinaturas/resumo").then((r) => r.data),
  criar: (a: AssinaturaInput) => http.post<Assinatura>("/assinaturas", a).then((r) => r.data),
  atualizar: (id: number, a: AssinaturaInput) =>
    http.put<Assinatura>(`/assinaturas/${id}`, a).then((r) => r.data),
  excluir: (id: number) => http.delete(`/assinaturas/${id}`),
};
