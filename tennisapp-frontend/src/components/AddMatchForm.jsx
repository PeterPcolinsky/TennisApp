import { useState } from "react";
import { api } from "../services/api";

export default function AddMatchForm({ onMatchAdded }) {
  const [playerA, setPlayerA] = useState("");
  const [playerB, setPlayerB] = useState("");
  const [score, setScore] = useState("");
  const [date, setDate] = useState("");
  const [message, setMessage] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!playerA || !playerB || !score || !date) {
      setMessage("⚠️ Vyplň všetky polia!");
      return;
    }

    const match = { playerA, playerB, score, date };

    try {
      await api.addMatch(match);
      setMessage("✅ Zápas bol pridaný!");
      setPlayerA("");
      setPlayerB("");
      setScore("");
      setDate("");

      if (onMatchAdded) onMatchAdded(); // 🔁 refresh tabuľky po pridaní
    } catch (err) {
      setMessage("❌ Chyba pri ukladaní: " + err.message);
    }
  };

  return (
    <div style={{ marginBottom: 30 }}>
      <form
        onSubmit={handleSubmit}
        style={{ display: "flex", gap: "8px", flexWrap: "wrap" }}
      >
        <input
          type="text"
          placeholder="Hráč A"
          value={playerA}
          onChange={(e) => setPlayerA(e.target.value)}
        />
        <input
          type="text"
          placeholder="Hráč B"
          value={playerB}
          onChange={(e) => setPlayerB(e.target.value)}
        />
        <input
          type="text"
          placeholder="Výsledok (napr. 6:4, 6:3)"
          value={score}
          onChange={(e) => setScore(e.target.value)}
        />
        <input
          type="date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
        />
        <button type="submit">Pridať zápas</button>
      </form>
      {message && <p style={{ marginTop: 10 }}>{message}</p>}
    </div>
  );
}