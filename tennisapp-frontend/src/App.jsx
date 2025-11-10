import { useState } from "react";
import AddPlayerForm from "./components/AddPlayerForm";
import PlayersTable from "./components/PlayersTable";
import LeaderboardTable from "./components/LeaderboardTable";
import MatchesTable from "./components/MatchesTable";

export default function App() {
  const [refreshKey, setRefreshKey] = useState(0);
  const refreshPlayers = () => setRefreshKey((k) => k + 1);

  return (
    <div style={{ fontFamily: "system-ui, Arial", padding: 20, color: "white" }}>
      <h1>
        TennisMate 🎾
      </h1>

      {/* --- Pridanie hráča --- */}
      <AddPlayerForm onPlayerAdded={refreshPlayers} />

      {/* --- Zoznam hráčov --- */}
      <div style={{ marginTop: 20 }}>
        <PlayersTable key={refreshKey} />
      </div>

      {/* --- Rebríček hráčov --- */}
      <div style={{ marginTop: 40 }}>
        <LeaderboardTable />
      </div>

      {/* --- Zoznam zápasov --- */}
      <div style={{ marginTop: 40 }}>
        <MatchesTable />
      </div>
    </div>
  );
}