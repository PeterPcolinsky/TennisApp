import { useState } from "react";
import AddPlayerForm from "./components/AddPlayerForm";
import PlayersTable from "./components/PlayersTable";
import LeaderboardTable from "./components/LeaderboardTable";
import MatchesTable from "./components/MatchesTable";
import AddMatchForm from "./components/AddMatchForm";
import LoginForm from "./components/LoginForm";
import { api } from "./services/api";

export default function App() {
  const [refreshPlayersKey, setRefreshPlayersKey] = useState(0);
  const [refreshMatchesKey, setRefreshMatchesKey] = useState(0);
  const [refreshLeaderboardKey, setRefreshLeaderboardKey] = useState(0);

  const refreshPlayers = () => setRefreshPlayersKey((k) => k + 1);
  const refreshMatches = () => setRefreshMatchesKey((k) => k + 1);
  const refreshLeaderboard = () => setRefreshLeaderboardKey((k) => k + 1);

  const username = sessionStorage.getItem("username");
  const isLogged = !!username;
  const isAdmin = username === "admin";

  // Keď sa zmenia zápasy (pridanie / mazanie), chceme refreshnúť aj leaderboard
  const handleMatchesChanged = () => {
    refreshMatches();
    refreshLeaderboard();
  };

  return (
    <div style={{ fontFamily: "system-ui, Arial", padding: 20, color: "white" }}>
      {/* HEADER */}
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

      {/* LOGIN / LOGOUT PANEL */}
      <div
        style={{
          marginTop: 20,
          marginBottom: 20,
          padding: 10,
          background: "#222",
          borderRadius: 8,
        }}
      >
        {!isLogged ? (
          <LoginForm onLogin={() => window.location.reload()} />
        ) : (
          <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
            <span>
              🔐 Prihlásený ako: <b>{username}</b>
            </span>

            <button
              onClick={() => {
                api.logout();
                window.location.reload();
              }}
              style={{ padding: "6px 12px" }}
            >
              Odhlásiť
            </button>
          </div>
        )}
      </div>

      {/* Title */}
      <h1 style={{ marginBottom: 30 }}>TennisMate 🎾</h1>

      {/* --- ADMIN ONLY: Pridanie hráča --- */}
      {isAdmin && (
        <section>
          <h2>➕ Pridať hráča</h2>
          <AddPlayerForm onPlayerAdded={refreshPlayers} />
        </section>
      )}

      {/* --- Zoznam hráčov --- */}
      <section>
        <h2>👥 Zoznam hráčov</h2>
        <PlayersTable key={refreshPlayersKey} canDelete={isAdmin} />
      </section>

      {/* --- Rebríček hráčov --- */}
      <section>
        <h2>🏆 Rebríček hráčov</h2>
        <LeaderboardTable key={refreshLeaderboardKey} />
      </section>

      {/* --- Zoznam zápasov --- */}
      <section>
        <h2>📋 Zoznam zápasov</h2>
        <MatchesTable
          key={refreshMatchesKey}
          canDelete={isAdmin}
          onMatchesChanged={handleMatchesChanged}
        />
      </section>

      {/* --- ADMIN ONLY: Pridať zápas --- */}
      {isAdmin && (
        <section>
          <h2>➕ Pridať zápas</h2>
          <AddMatchForm onMatchAdded={handleMatchesChanged} />
        </section>
      )}

      {/* --- FOOTER --- */}
      <footer className="footer">
        <p>© {new Date().getFullYear()} Peter Pčolinský – Všetky práva vyhradené.</p>
        <p className="dev-note">Developed by Peter Pčolinský</p>
      </footer>
    </div>
  );
}