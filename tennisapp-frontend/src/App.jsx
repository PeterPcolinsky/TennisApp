import { useState } from 'react';
import PlayersTable from './components/PlayersTable';
import AddPlayerForm from './components/AddPlayerForm';

export default function App() {
  const [refreshKey, setRefreshKey] = useState(0);
  const refreshPlayers = () => setRefreshKey((k) => k + 1);

  return (
    <div style={{ fontFamily: 'system-ui, Arial', padding: 20 }}>
      <h1>TennisApp – React frontend 🎾</h1>

      <AddPlayerForm onPlayerAdded={refreshPlayers} />
      <div style={{ marginTop: 20 }}>
        <PlayersTable key={refreshKey} />
      </div>
    </div>
  );
}