import { useState } from "react";
import { setAuth } from "../services/api";

export default function LoginForm({ onLogin }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const handleLogin = (e) => {
    e.preventDefault();

    if (!username || !password) {
      setMessage("Zadaj meno aj heslo.");
      return;
    }

    // 🔐 nastav Basic Auth pre všetky ďalšie API volania
    setAuth(username, password);
    setMessage(`Prihlásený ako: ${username}`);
    onLogin?.(username);
  };

  return (
    <div style={{ marginBottom: 20, background: "#222", padding: 15, borderRadius: 8 }}>
      <h3>🔐 Prihlásenie</h3>

      <form onSubmit={handleLogin} style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
        <input
          type="text"
          placeholder="Meno (admin / user)"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          style={{ padding: 8 }}
        />
        <input
          type="password"
          placeholder="Heslo"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          style={{ padding: 8 }}
        />

        <button type="submit" style={{ padding: "8px 12px" }}>
          Prihlásiť
        </button>
      </form>

      {message && <p style={{ marginTop: 10 }}>{message}</p>}

      <p style={{ marginTop: 10, fontSize: 13, opacity: 0.7 }}>
        * GET požiadavky sú verejné, ale na POST/PUT/DELETE potrebuješ prihlásenie.
      </p>
    </div>
  );
}