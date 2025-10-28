package sk.peter.tenis.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldListMatches() throws Exception {
        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Integračný test jedným ťahom:
     * 1) POST vytvorí zápas (Peter vs Miroslav, 2025-06-01, "6:4, 6:4")
     * 2) PUT ho aktualizuje na nový dátum + skóre
     * 3) DELETE ho zmaže podľa nových hodnôt
     * <p>
     * Pozn.: hráči "Peter" a "Miroslav" sú v tvojom players.csv,
     * dátumy volíme také, aby neboli v CSV kolízie.
     */
    @Test
    void shouldCreateUpdateThenDeleteMatch() throws Exception {
        // 1) CREATE
        String createJson = """
                {
                  "playerA": "Peter",
                  "playerB": "Miroslav",
                  "score": "6:4, 6:4",
                  "date": "2025-06-01"
                }
                """;

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated());

        // 2) UPDATE (identifikujeme pôvodné hodnoty cez request parametre, telo je MatchUpdateDto)
        String updateJson = """
                {
                  "newScore": "7:6, 6:4",
                  "newDate": "2025-06-02"
                }
                """;

        mockMvc.perform(put("/api/matches")
                        .param("playerA", "Peter")
                        .param("playerB", "Miroslav")
                        .param("score", "6:4, 6:4")   // pôvodné skóre
                        .param("date", "2025-06-01") // pôvodný dátum
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // 3) DELETE (po update mažeme už podľa NOVÝCH hodnôt)
        mockMvc.perform(delete("/api/matches")
                        .param("playerA", "Peter")
                        .param("playerB", "Miroslav")
                        .param("score", "7:6, 6:4")   // nové skóre
                        .param("date", "2025-06-02")) // nový dátum
                .andExpect(status().isNoContent());
    }

    // ==========================
// 🔴 NEGATÍVNE SCENÁRE
// ==========================

    @Test
    void shouldFailToCreateMatch_whenPlayerDoesNotExist() throws Exception {
        // Hráč "Neznamy" nie je v players.csv -> očakávame 400 (IllegalArgumentException -> handler)
        String json = """
                {
                  "playerA": "Neznamy",
                  "playerB": "Peter",
                  "score": "6:4, 6:4",
                  "date": "2025-06-10"
                }
                """;

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFound_onUpdateMissingMatch() throws Exception {
        // Neexistujúci zápas (kombinácia parametrov), očakávame 404 (NotFoundException -> handler)
        String body = """
                { "newScore": "6:0, 6:0", "newDate": "2025-07-01" }
                """;

        mockMvc.perform(put("/api/matches")
                        .param("playerA", "Peter")
                        .param("playerB", "Miroslav")
                        .param("score", "1:6, 2:6")   // pôvodné, ktoré neexistuje
                        .param("date", "2025-01-01")  // pôvodný dátum, ktorý neexistuje
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFound_onDeleteMissingMatch() throws Exception {
        // Neexistujúci zápas -> 404
        mockMvc.perform(delete("/api/matches")
                        .param("playerA", "Roger")
                        .param("playerB", "Novak")
                        .param("score", "0:6, 0:6")
                        .param("date", "2024-12-31"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFilterByPlayer() throws Exception {
        mockMvc.perform(get("/api/matches/filter").param("player", "Novak"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].playerA.name", hasItem(anyOf(is("Novak"), is("Peter"), is("Miroslav"), is("Roger")))))
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    void shouldFilterByDateRange() throws Exception {
        // v CSV máš 2025-01-08, 2025-04-13, 2025-05-05, 2025-05-10
        mockMvc.perform(get("/api/matches/filter")
                        .param("from", "2025-05-01")
                        .param("to", "2025-05-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2))); // 2025-05-05 a 2025-05-10
    }

    @Test
    void shouldFilterByPlayerAndDateRange() throws Exception {
        mockMvc.perform(get("/api/matches/filter")
                        .param("player", "Peter")
                        .param("from", "2025-04-01")
                        .param("to", "2025-05-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2))); // Peter má 2025-04-13 a 2025-05-10
    }
}