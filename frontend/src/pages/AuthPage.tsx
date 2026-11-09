import { useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { mensagemErro } from "../api/client";
import { useAuth } from "../context/AuthContext";

export default function AuthPage({ modo }: { modo: "login" | "cadastro" }) {
  const { login, registrar } = useAuth();
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [enviando, setEnviando] = useState(false);
  const cadastro = modo === "cadastro";

  const enviar = async (e: FormEvent) => {
    e.preventDefault();
    setErro("");
    if (cadastro && senha.length < 6) return setErro("A senha deve ter no mínimo 6 caracteres.");
    setEnviando(true);
    try {
      if (cadastro) await registrar(nome, email, senha);
      else await login(email, senha);
    } catch (err) {
      setErro(mensagemErro(err));
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div className="auth">
      <div className="card auth-card">
        <h1 className="logo grande">Assinô</h1>
        <p className="muted">Controle suas assinaturas e gastos mensais</p>
        <form onSubmit={enviar} className="form">
          {cadastro && (
            <label>Nome<input value={nome} onChange={(e) => setNome(e.target.value)} required maxLength={100} /></label>
          )}
          <label>E-mail<input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required /></label>
          <label>Senha<input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} required /></label>
          {erro && <p className="erro">{erro}</p>}
          <button className="btn" disabled={enviando}>
            {enviando ? "Aguarde..." : cadastro ? "Criar conta" : "Entrar"}
          </button>
        </form>
        <p className="muted pequeno">
          {cadastro ? <>Já tem conta? <Link to="/login">Entrar</Link></> : <>Não tem conta? <Link to="/cadastro">Cadastre-se</Link></>}
        </p>
      </div>
    </div>
  );
}
