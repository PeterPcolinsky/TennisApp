import { useState } from 'react';
import { api } from '../services/api';

// Len písmená + medzery, aj s diakritikou
const nameRegex = /^[A-Za-zÀ-ž]+(?: [A-Za-zÀ-ž]+)*$/;

export default function AddPlayerForm({ onPlayerAdded }) {
  const [name, setName] = useState('');
  const [age, setAge] = useState('');
  const [type, setType] = useState('PROFESIONAL');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');

    const trimmedName = name.trim();
    const ageNum = Number(age);

    // 🔥 VALIDÁCIA MENA
    if (!trimmedName) {
      setError("❌ Zadaj meno hráča.");
      return;
    }

    if (!nameRegex.test(trimmedName)) {
      setError("❌ Meno môže obsahovať len písmená a medzery (bez číslic a špeciálnych znakov).");
      return;
    }

    // 🔥 VALIDÁCIA VEKU
    if (!Number.isInteger(ageNum) || ageNum < 5 || ageNum > 100) {
      setError("❌ Zadaj reálny vek hráča (5 až 100 rokov).");
      return;
    }

    setLoading(true);

    try {
      await api.addPlayer({ name: trimmedName, age: ageNum, type });

      // reset
      setName('');
      setAge('');
      setType('PROFESIONAL');

      onPlayerAdded?.();

    } catch (err) {

      // 🔥 DEBUG — TOTO POTREBUJEME VIDIEŤ
      console.log("🔥 err:", err);
      console.log("🔥 err.message:", err.message);
      console.log("🔥 err.response:", err.response);
      console.log("🔥 err.toString():", err.toString());

      // Dočasne zobrazíme priamo message
      setError("❌ " + err.message);

    } finally {
      setLoading(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} style={{ display: 'grid', gap: 10, maxWidth: 320 }}>
      {error && <div style={{ color: 'red', fontWeight: 'bold' }}>{error}</div>}

      <label>
        Meno:
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          disabled={loading}
          placeholder="Napr. Roger Federer"
          required
        />
      </label>

      <label>
        Vek:
        <input
          type="number"
          value={age}
          onChange={(e) => setAge(e.target.value)}
          disabled={loading}
          placeholder="Vek hráča"
          required
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