import { createContext, useCallback, useContext, useEffect, useState, type ReactNode } from "react";
import { authApi, tokenStorage } from "../api/client";
import type { Usuario } from "../api/types";

interface AuthState {
  usuario: Usuario | null;
  carregando: boolean;
  login: (email: string, senha: string) => Promise<void>;
  registrar: (nome: string, email: string, senha: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [carregando, setCarregando] = useState(true);

  const carregarPerfil = useCallback(async () => {
    if (!tokenStorage.get()) {
      setCarregando(false);
      return;
    }
    try {
      setUsuario(await authApi.perfil());
    } catch {
      tokenStorage.clear();
      setUsuario(null);
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    carregarPerfil();
    const onLogout = () => setUsuario(null);
    window.addEventListener("assino:logout", onLogout);
    return () => window.removeEventListener("assino:logout", onLogout);
  }, [carregarPerfil]);

  const login = async (email: string, senha: string) => {
    const { token } = await authApi.login(email, senha);
    tokenStorage.set(token);
    setUsuario(await authApi.perfil());
  };

  const registrar = async (nome: string, email: string, senha: string) => {
    const { token } = await authApi.registrar(nome, email, senha);
    tokenStorage.set(token);
    setUsuario(await authApi.perfil());
  };

  const logout = () => {
    tokenStorage.clear();
    setUsuario(null);
  };

  return (
    <AuthContext.Provider value={{ usuario, carregando, login, registrar, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth deve ser usado dentro de AuthProvider");
  return ctx;
}
