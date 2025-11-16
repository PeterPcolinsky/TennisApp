import { useState } from "react";
import { api } from "../services/api";

const nameRegex = /^[A-Za-zÀ-ž]+(?: [A-Za-zÀ-ž]+)*$/; // písmená + medzery, žiadne čísla

function validateScore(rawScore) {
  const score = rawScore.trim();
  if (!score) {
    return { ok: false, msg: "Vyplň výsledok zápasu." };
  }

  const sets = score.split(",");
  if (!sets.length) {
    return { ok: false, msg: "Výsledok musí obsahovať aspoň jeden set (napr. 6:4)." };
  }

  for (const part of sets) {
    const s = part.trim();
    const match = s.match(/^(\d{1,2}):(\d{1,2})$/);

    if (!match) {
      return {
        ok: false,
        msg: "Neplatný formát výsledku. Použi napr. 6:4 alebo 6:4, 7:6.",
      };
    }

    const gamesA = parseInt(match[1], 10);
    const gamesB = parseInt(match[2], 10);

    if (gamesA === gamesB) {
      return { ok: false, msg: "Set nemôže skončiť remízou (napr. 6:6 je neplatné)." };
    }

    const max = Math.max(gamesA, gamesB);
    const min = Math.min(gamesA, gamesB);

    if (max < 6) {
      return {
        ok: false,
        msg: "Víťaz setu musí mať aspoň 6 gemov (napr. 6:4, 7:5, 7:6).",
      };
    }

    if (max === 6 && max - min < 2) {
      return {
        ok: false,
        msg: "Pri 6 gemoch musí byť rozdiel aspoň 2 (napr. 6:4, 6:3).",
      };
    }

    if (max === 7 && min < 5) {
      return {
        ok: false,
        msg: "Set 7:x je možný len pri 7:5 alebo 7:6 (7:4 je neplatné).",
      };
    }

    if (max > 7) {
      return {
        ok: false,
        msg: "Počet gemov v sete je príliš vysoký. Zadaj reálny tenisový výsledok.",
      };
    }
  }

  return { ok: true };
}

export default function AddMatchForm({ onMatchAdded }) {
  const [playerA, setPlayerA] = useState("");
  const [playerB, setPlayerB] = useState("");
  const [score, setScore] = useState("");
  const [date, setDate] = useState("");
  const [message, setMessage] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    const pA = playerA.trim();
    const pB = playerB.trim();

    if (!pA || !pB || !score || !date) {
      setMessage("⚠️ Vyplň všetky polia!");
      return;
    }

    if (!nameRegex.test(pA) || !nameRegex.test(pB)) {
      setMessage("❌ Meno hráča môže obsahovať len písmená a medzery (bez číslic a zvláštnych znakov).");
      return;
    }

    if (pA.toLowerCase() === pB.toLowerCase()) {
      setMessage("❌ Hráč A a Hráč B musia byť rozdielni.");
      return;
    }

    const scoreCheck = validateScore(score);
    if (!scoreCheck.ok) {
      setMessage("❌ " + scoreCheck.msg);
      return;
    }

    const match = { playerA: pA, playerB: pB, score: score.trim(), date };

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
          placeholder="Výsledok (napr. 6:4, 7:6)"
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