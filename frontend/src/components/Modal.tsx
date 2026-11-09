import { useEffect, type ReactNode } from "react";

interface Props {
  titulo: string;
  aberto: boolean;
  onFechar: () => void;
  children: ReactNode;
}

export default function Modal({ titulo, aberto, onFechar, children }: Props) {
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => e.key === "Escape" && onFechar();
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [onFechar]);

  if (!aberto) return null;
  return (
    <div className="modal-fundo" onMouseDown={onFechar}>
      <div className="modal" role="dialog" aria-modal="true" onMouseDown={(e) => e.stopPropagation()}>
        <div className="modal-topo">
          <h2>{titulo}</h2>
          <button className="btn-icone" onClick={onFechar} aria-label="Fechar">✕</button>
        </div>
        {children}
      </div>
    </div>
  );
}
