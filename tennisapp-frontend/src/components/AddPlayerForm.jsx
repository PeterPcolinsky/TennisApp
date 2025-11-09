import { useState } from 'react';
import { api } from '../services/api';

export default function AddPlayerForm({ onPlayerAdded }) {
  const [name, setName] = useState('');
  const [age, setAge] = useState('');
  const [type, setType] = useState('PROFESIONAL');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await api.addPlayer({ name: name.trim(), age: Number(age), type });
      setName('');
      setAge('');
      setType('PROFESIONAL');
      onPlayerAdded?.(); // po úspechu obnovíme tabuľku
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} style={{ display: 'grid', gap: 10, maxWidth: 320 }}>
      <h2>Pridať hráča</h2>
      {error && <div style={{ color: 'red' }}>Chyba: {error}</div>}
      <label>
        Meno:
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
          disabled={loading}
        />
      </label>

      <label>
        Vek:
        <input
          type="number"
          value={age}
          onChange={(e) => setAge(e.target.value)}
          required
          disabled={loading}
        />
      </label>

      <label>
        Typ:
        <select
          value={type}
          onChange={(e) => setType(e.target.value)}
          disabled={loading}
        >
          <option value="PROFESIONAL">PROFESIONAL</option>
          <option value="AMATER">AMATER</option>
        </select>
      </label>

      <button type="submit" disabled={loading}>
        {loading ? 'Ukladám...' : 'Pridať hráča'}
      </button>
    </form>
  );
}