import { useEffect, useState } from "react";
import { api } from "../services/api";

export default function MatchesTable({ canDelete, onMatchesChanged }) {
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchMatches = async () => {
      try {
        const data = await api.getMatches();
        setMatches(Array.isArray(data) ? data : []);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchMatches();
  }, []);

  const deleteMatch = async (id) => {
    if (!window.confirm("Naozaj chceš zmazať tento zápas?")) return;
    try {
      await api.deleteMatch(id);
      setMatches((prev) => prev.filter((m) => m.id !== id));
      if (onMatchesChanged) onMatchesChanged(); // 🔁 refresh leaderboardu + prípadne ďalšie
    } catch (err) {
      alert("Chyba pri mazaní: " + err.message);
    }
  };

  if (loading) return <p>Načítavam zápasy...</p>;
  if (error) return <p style={{ color: "red" }}>Chyba: {error}</p>;
  if (!matches.length) return <p>Žiadne zápasy zatiaľ nie sú dostupné.</p>;

  return (
    <div>
      <table border="1" cellPadding="6">
        <thead>
          <tr>
            <th>ID</th>
            <th>Hráč A</th>
            <th>Hráč B</th>
            <th>Výsledok</th>
            <th>Dátum</th>
            {canDelete && <th>Akcia</th>}
          </tr>
        </thead>
        <tbody>
          {matches.map((m) => (
            <tr key={m.id}>
              <td>{m.id}</td>
              <td>{m.playerAName}</td>
              <td>{m.playerBName}</td>
              <td>{m.score}</td>
              <td>{m.date}</td>
              {canDelete && (
                <td>
                  <button className="delete" onClick={() => deleteMatch(m.id)}>
                    Vymazať
                  </button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}