const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

async function handle(res) {
  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(`HTTP ${res.status} – ${text || res.statusText}`);
  }
  const contentType = res.headers.get('content-type') || '';
  return contentType.includes('application/json') ? res.json() : res.text();
}

export const api = {
  async health() {
    return handle(await fetch(`${BASE_URL}/api/health`));
  },
  async getPlayers() {
    return handle(await fetch(`${BASE_URL}/api/players`));
  },
  async addPlayer(player) {
    return handle(await fetch(`${BASE_URL}/api/players`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(player),
    }));
  },
  async deletePlayer(name) {
    return handle(await fetch(`${BASE_URL}/api/players/${encodeURIComponent(name)}`, {
      method: 'DELETE',
    }));
  },
  async leaderboard() {
    return handle(await fetch(`${BASE_URL}/api/stats/leaderboard`));
  },
  async playerStats({ name, from, to }) {
    const params = new URLSearchParams({ name });
    if (from) params.append('from', from);
    if (to) params.append('to', to);
    return handle(await fetch(`${BASE_URL}/api/stats/player?${params.toString()}`));
  },
};