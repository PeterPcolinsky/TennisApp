import { useState } from "react";
import AddPlayerForm from "./components/AddPlayerForm";
import PlayersTable from "./components/PlayersTable";
import LeaderboardTable from "./components/LeaderboardTable";
import MatchesTable from "./components/MatchesTable";
import AddMatchForm from "./components/AddMatchForm";

export default function App() {
  const [refreshKey, setRefreshKey] = useState(0);
  const refreshPlayers = () => setRefreshKey((k) => k + 1);

  return (
    <div style={{ fontFamily: "system-ui, Arial", padding: 20, color: "white" }}>
        <header className="navbar">
          <div className="navbar-content">
            <span className="logo">🎾 TennisMate</span>
            <nav>
              <a href="#">Domov</a>
              <a href="#">O aplikácii</a>
              <a href="#">Kontakt</a>
            </nav>
          </div>
        </header>
      <h1 style={{ marginBottom: 30 }}>TennisMate 🎾</h1>

      {/* --- Pridanie hráča --- */}
      <section>
        <h2>➕ Pridať hráča</h2>
        <AddPlayerForm onPlayerAdded={refreshPlayers} />
      </section>

      {/* --- Zoznam hráčov --- */}
      <section>
        <h2>👥 Zoznam hráčov</h2>
        <PlayersTable key={refreshKey} />
      </section>

      {/* --- Rebríček hráčov --- */}
      <section>
        <h2>🏆 Rebríček hráčov</h2>
        <LeaderboardTable />
      </section>

      {/* --- Zoznam zápasov --- */}
      <section>
        <h2>📋 Zoznam zápasov</h2>
        <MatchesTable />
      </section>

      {/* --- Pridať zápas --- */}
      <section>
        <h2>➕ Pridať zápas</h2>
        <AddMatchForm />
      </section>

      {/* --- FOOTER --- */}
            <footer className="footer">
              <p>© {new Date().getFullYear()} Peter Pčolinský – Všetky práva vyhradené.</p>
              <p className="dev-note">Developed by Peter Pčolinský</p>
            </footer>
    </div>
  );
}