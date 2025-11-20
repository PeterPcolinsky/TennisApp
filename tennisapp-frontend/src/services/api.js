const BASE_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8081";

let authUser = null;
let authPass = null;

// 🔐 Nastavenie Basic Auth z frontendu
export function setAuth(username, password) {
  authUser = username;
  authPass = password;
}

// 🔐 Okamžité vymazanie Basic Auth pri odhlásení
export function clearAuthImmediately() {
  authUser = null;
  authPass = null;
}

function buildHeaders(extra = {}) {
  const headers = { ...extra };

  const user = sessionStorage.getItem("username");
  const pass = sessionStorage.getItem("password");

  if (user && pass) {
    const encoded = btoa(`${user}:${pass}`);
    headers["Authorization"] = `Basic ${encoded}`;
  }

  return headers;
}

//  ERROR HANDLER
async function handle(res) {
  if (!res.ok) {

    // 401 a 403
    if (res.status === 401) throw new Error("Nie si prihlásený.");
    if (res.status === 403) throw new Error("Nemáš oprávnenie vykonať túto akciu.");

    // zistíme typ odpovede
    const contentType = res.headers.get("content-type") || "";

    // 🔥 PRI CHYBE BACKEND POSIELA JSON
    if (contentType.includes("application/json")) {
      const data = await res.json().catch(() => null);

      if (data && data.error) {
        throw new Error(data.error);
      }

      throw new Error("Neznáma chyba (JSON bez erroru)");
    }

    // 🔥 ak to nie je JSON → treat as text
    const text = await res.text().catch(() => "");

    if (text) {
      throw new Error(text.trim());
    }

    throw new Error("Neznáma chyba");
  }

  // success
  const contentType = res.headers.get("content-type") || "";
  return contentType.includes("application/json") ? res.json() : res.text();
}

export const api = {
  // --- Health ---
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
        headers: buildHeaders({ "Content-Type": "application/json" }),
        body: JSON.stringify(player),
      })
    );
  },

  async deletePlayer(name) {
    return handle(
      await fetch(`${BASE_URL}/api/players/${encodeURIComponent(name)}`, {
        method: "DELETE",
        headers: buildHeaders(),
      })
    );
  },

  // --- Leaderboard ---
  async getLeaderboard() {
    return handle(await fetch(`${BASE_URL}/api/stats/leaderboard`));
  },

  // --- Player Stats ---
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
        headers: buildHeaders(),
      })
    );
  },

  async addMatch(match) {
    return handle(
      await fetch(`${BASE_URL}/api/matches`, {
        method: "POST",
        headers: buildHeaders({ "Content-Type": "application/json" }),
        body: JSON.stringify(match),
      })
    );
  },

  // --- Logout ---
  logout() {
    authUser = null;
    authPass = null;
    sessionStorage.removeItem("username");
    sessionStorage.removeItem("password");
  }
};