import { useEffect, useState } from "react";
import { api } from "../services/api";

export default function LeaderboardTable() {
  const [leaderboard, setLeaderboard] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchLeaderboard = async () => {
      try {
        const data = await api.getLeaderboard();
        setLeaderboard(Array.isArray(data) ? data : []);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchLeaderboard();
  }, []);

  if (loading) return <p>Načítavam leaderboard...</p>;
  if (error) return <p style={{ color: "red" }}>Chyba: {error}</p>;
  if (!leaderboard.length) return <p>Žiadne štatistiky zatiaľ nie sú dostupné.</p>;

  return (
    <div>
      <table border="1" cellPadding="6">
        <thead>
          <tr>
            <th>Poradie</th>
            <th>Meno</th>
            <th>Zápasy</th>
            <th>Výhry</th>
            <th>Prehry</th>
            <th>Úspešnosť (%)</th>
          </tr>
        </thead>
        <tbody>
          {leaderboard.map((p, index) => (
            <tr key={p.name}>
              <td>{index + 1}</td>
              <td>{p.name}</td>
              <td>{p.matches}</td>
              <td>{p.wins}</td>
              <td>{p.losses}</td>
              <td>{p.winRatePercent.toFixed(1)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}