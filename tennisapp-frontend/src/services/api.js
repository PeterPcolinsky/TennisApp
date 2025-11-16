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

export async function forceBrowserLogout() {
  try {
    await fetch("http://localhost:8081/api/health", {
      headers: {
        "Authorization": "Basic " + btoa("logout:logout")
      }
    });
  } catch (_) {}
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

async function handle(res) {
  if (!res.ok) {
    if (res.status === 401) {
      throw new Error("Nie si prihlásený.");
    }
    if (res.status === 403) {
      throw new Error("Nemáš oprávnenie vykonať túto akciu.");
    }

    const text = await res.text().catch(() => "");
    throw new Error(`Chyba: ${text || res.statusText}`);
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

  // --- Player stats ---
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

  // 🔐 Logout – + čistenie BasicAuth tokenov
  logout() {
    authUser = null;
    authPass = null;

    sessionStorage.removeItem("username");
    sessionStorage.removeItem("password");
  },
};