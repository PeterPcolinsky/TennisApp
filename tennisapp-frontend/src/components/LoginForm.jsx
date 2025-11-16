import { useState } from "react";
import { setAuth, clearAuthImmediately } from "../services/api";

export default function LoginForm({ onLogin }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();

    if (!username || !password) {
      setMessage("Zadaj meno aj heslo.");
      return;
    }

    try {
      const encoded = btoa(`${username}:${password}`);

      // 🔥 PRIAMY request na backend origin (nutné)
      const res = await fetch("http://localhost:8081/api/health", {
        method: "GET",
        headers: {
          "Authorization": `Basic ${encoded}`,
          "X-Requested-With": "XMLHttpRequest" // 🔥 blokuje browser popup pri 401
        }
      });

      if (!res.ok) {
        throw new Error("Unauthorized");
      }

      // 🔥 úspech – uložiť session
      sessionStorage.setItem("username", username);
      sessionStorage.setItem("password", password);

      // 🔥 uložiť auth pre všetky ďalšie fetch requesty
      setAuth(username, password);

      setMessage("Prihlásenie úspešné ✔");

      onLogin(username);

    } catch (err) {
      // ❗ nutné úplné odhlásenie pri neúspechu
      clearAuthImmediately();
      sessionStorage.removeItem("username");
      sessionStorage.removeItem("password");

      setMessage("❌ Nesprávne meno alebo heslo.");
    }
  };

  return (
    <div style={{ marginBottom: 20 }}>
      <form onSubmit={handleLogin} style={{ display: "flex", gap: 8 }}>
        <input
          type="text"
          placeholder="Meno"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="password"
          placeholder="Heslo"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button type="submit">Prihlásiť</button>
      </form>

      {message && <p style={{ marginTop: 10 }}>{message}</p>}
    </div>
  );
}