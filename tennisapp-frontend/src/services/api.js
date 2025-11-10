const BASE_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8081";

async function handle(res) {
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(`HTTP ${res.status} – ${text || res.statusText}`);
  }
  const contentType = res.headers.get("content-type") || "";
  return contentType.includes("application/json") ? res.json() : res.text();
}

export const api = {
  // --- Health endpoint ---
  async health() {
    return handle(await fetch(`${BASE_URL}/api/health`));
  },

  // --- Players ---
  async getPlayers() {
    return handle(await fetch(`${BASE_URL}/api/players`));
  },

  async addPlayer(player) {
    return handle(
      await fetch(`${BASE_URL}/api/players`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(player),
      })
    );
  },

  async deletePlayer(name) {
    return handle(
      await fetch(`${BASE_URL}/api/players/${encodeURIComponent(name)}`, {
        method: "DELETE",
      })
    );
  },

  // --- Leaderboard ---
  async getLeaderboard() {
    return handle(await fetch(`${BASE_URL}/api/stats/leaderboard`));
  },

  // --- Player stats (optional) ---
  async playerStats({ name, from, to }) {
    const params = new URLSearchParams({ name });
    if (from) params.append("from", from);
    if (to) params.append("to", to);
    return handle(
      await fetch(`${BASE_URL}/api/stats/player?${params.toString()}`)
    );
  },

  // --- Matches ---
  async getMatches() {
    return handle(await fetch(`${BASE_URL}/api/matches`));
  },

  async deleteMatch(id) {
    return handle(
      await fetch(`${BASE_URL}/api/matches/${id}`, {
        method: "DELETE",
      })
    );
  },
};