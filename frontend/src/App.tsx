import { Navigate, Outlet, Route, Routes } from "react-router-dom";
import { useAuth } from "./context/AuthContext";
import Layout from "./components/Layout";
import AuthPage from "./pages/AuthPage";
import Dashboard from "./pages/Dashboard";
import Assinaturas from "./pages/Assinaturas";

function RotaPrivada() {
  const { usuario, carregando } = useAuth();
  if (carregando) return <div className="tela-carregando">Carregando...</div>;
  return usuario ? <Outlet /> : <Navigate to="/login" replace />;
}

function RotaPublica() {
  const { usuario, carregando } = useAuth();
  if (carregando) return <div className="tela-carregando">Carregando...</div>;
  return usuario ? <Navigate to="/" replace /> : <Outlet />;
}

export default function App() {
  return (
    <Routes>
      <Route element={<RotaPublica />}>
        <Route path="/login" element={<AuthPage modo="login" />} />
        <Route path="/cadastro" element={<AuthPage modo="cadastro" />} />
      </Route>
      <Route element={<RotaPrivada />}>
        <Route element={<Layout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/assinaturas" element={<Assinaturas />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
