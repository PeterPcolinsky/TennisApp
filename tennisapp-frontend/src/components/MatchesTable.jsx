import { useEffect, useState } from "react";
import { api } from "../services/api";

export default function MatchesTable() {
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchMatches = async () => {
      try {
        const data = await api.getMatches();
        console.log("🎾 FETCHED MATCHES:", data);
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
      setMatches(matches.filter((m) => m.id !== id));
    } catch (err) {
      alert("Chyba pri mazaní: " + err.message);
    }
  };

  if (loading) return <p>Načítavam zápasy...</p>;
  if (error) return <p style={{ color: "red" }}>Chyba: {error}</p>;
  if (!matches.length) return <p>Žiadne zápasy zatiaľ nie sú dostupné.</p>;

  return (
    <div>
      <h2>📋 Zoznam zápasov</h2>
      <table border="1" cellPadding="6">
        <thead>
          <tr>
            <th>ID</th>
            <th>Hráč A</th>
            <th>Hráč B</th>
            <th>Výsledok</th>
            <th>Dátum</th>
            <th>Akcia</th>
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
              <td>
                <button onClick={() => deleteMatch(m.id)}>Vymazať</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}