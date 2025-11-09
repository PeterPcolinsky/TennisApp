import { useEffect, useState } from 'react';
import { api } from '../services/api';

export default function PlayersTable() {
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    (async () => {
      try {
        const data = await api.getPlayers();
        setPlayers(Array.isArray(data) ? data : []);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  if (loading) return <p>Načítavam hráčov...</p>;
  if (error) return <p style={{ color: 'red' }}>Chyba: {error}</p>;
  if (!players.length) return <p>Žiadni hráči neboli nájdení.</p>;

  return (
    <div>
      <h2>Zoznam hráčov</h2>
      <table border="1" cellPadding="6">
        <thead>
          <tr>
            <th>Meno</th>
            <th>Vek</th>
            <th>Typ</th>
          </tr>
        </thead>
        <tbody>
          {players.map((p, i) => (
            <tr key={i}>
              <td>{p.name}</td>
              <td>{p.age}</td>
              <td>{p.type}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}