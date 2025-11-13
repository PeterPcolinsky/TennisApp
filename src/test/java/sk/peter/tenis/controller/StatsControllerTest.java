package sk.peter.tenis.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sk.peter.tenis.annotations.TestWithoutSecurity;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integračné testy pre StatsController – CSV režim (bez JPA).
 * Testuje štyri hlavné endpointy:
 * - /api/stats/leaderboard
 * - /api/stats/export
 * - /api/stats/top?limit=N
 * - /api/stats/player?name=...&from=...&to=...
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestWithoutSecurity
@ActiveProfiles("h2")
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 1️⃣ /api/stats/leaderboard – JSON leaderboard
    @Test
    void shouldReturnLeaderboard_sortedByWinRate() throws Exception {
        mockMvc.perform(get("/api/stats/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Kontrola top hráčov podľa CSV dát
                .andExpect(jsonPath("$[0].name", anyOf(is("Peter"), is("Roger"))))
                .andExpect(jsonPath("$[0].winRatePercent", is(100.0)))
                .andExpect(jsonPath("$[1].name", anyOf(is("Peter"), is("Roger"))))
                .andExpect(jsonPath("$[1].winRatePercent", is(100.0)))
                .andExpect(jsonPath("$[2].name", is("Novak")))
                .andExpect(jsonPath("$[2].winRatePercent", is(33.3)));
    }

    // 2️⃣ /api/stats/export – CSV export leaderboardu
    @Test
    void shouldExportLeaderboardCsv() throws Exception {
        mockMvc.perform(get("/api/stats/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("leaderboard.csv")))
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                // hlavička
                .andExpect(content().string(containsString("Meno;Zapasy;Vyhry;Prehry;WinRate(%)")))
                // typické riadky podľa CSV dát
                .andExpect(content().string(containsString("Peter;4;4;0;100.0")))
                .andExpect(content().string(containsString("Novak;3;1;2;33.3")));
    }

    // 3️⃣ /api/stats/top – len N top hráčov
    @Test
    void shouldReturnTopNPlayers() throws Exception {
        mockMvc.perform(get("/api/stats/top").param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].winRatePercent", is(100.0)));
    }

    // 4️⃣ /api/stats/player – štatistiky hráča s dátovým rozsahom
    @Test
    void shouldReturnPlayerStatsWithDateRange() throws Exception {
        mockMvc.perform(get("/api/stats/player")
                        .param("name", "Peter")
                        .param("from", "2025-04-01")
                        .param("to", "2025-05-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Peter")))
                .andExpect(jsonPath("$.matches", is(4)))
                .andExpect(jsonPath("$.wins", is(4)))
                .andExpect(jsonPath("$.losses", is(0)))
                .andExpect(jsonPath("$.winRatePercent", is(100.0)));
    }
}