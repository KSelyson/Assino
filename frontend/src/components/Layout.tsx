import { NavLink, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Layout() {
  const { usuario, logout } = useAuth();
  return (
    <div className="app">
      <header className="topbar">
        <div className="topbar-esq">
          <span className="logo">Assinô</span>
          <nav>
            <NavLink to="/" end>Painel</NavLink>
            <NavLink to="/assinaturas">Assinaturas</NavLink>
          </nav>
        </div>
        <div className="topbar-dir">
          <span className="muted">Olá, {usuario?.nome.split(" ")[0]}</span>
          <button className="btn btn-ghost" onClick={logout}>Sair</button>
        </div>
      </header>
      <main className="conteudo">
        <Outlet />
      </main>
    </div>
  );
}
