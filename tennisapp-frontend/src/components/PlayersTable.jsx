import { useEffect, useState } from 'react';
import { api, clearAuthImmediately } from '../services/api';

export default function PlayersTable() {
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // zisťujeme rolu
  const username = sessionStorage.getItem("username");
  const isAdmin = username === "admin";

  async function refresh() {
    try {
      const data = await api.getPlayers();
      setPlayers(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    refresh();
  }, []);

  async function deletePlayer(name) {

    // dvojitá ochrana
    if (!isAdmin) {
      alert("Nemáš oprávnenie zmazať hráča.");
      return;
    }

    if (!window.confirm(`Naozaj chceš vymazať hráča "${name}"?`)) return;

    try {
      await api.deletePlayer(name);
      await refresh();
    } catch (err) {
      alert(`Chyba pri mazaní: ${err.message}`);
    }
  }

  if (loading) return <p>Načítavam hráčov...</p>;
  if (error) return <p style={{ color: 'red' }}>Chyba: {error}</p>;
  if (!players.length) return <p>Žiadni hráči neboli nájdení.</p>;

  return (
    <div>
      <table border="1" cellPadding="6">
        <thead>
          <tr>
            <th>Meno</th>
            <th>Vek</th>
            <th>Typ</th>
            <th>Akcia</th>
          </tr>
        </thead>
        <tbody>
          {players.map((p, i) => (
            <tr key={i}>
              <td>{p.name}</td>
              <td>{p.age}</td>
              <td>{p.type}</td>
              <td>
                {isAdmin && (
                  <button className="delete" onClick={() => deletePlayer(p.name)}>
                    Vymazať
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}