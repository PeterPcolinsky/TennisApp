package sk.peter.tenis.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.equalToIgnoringCase;


@SpringBootTest
@AutoConfigureMockMvc
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAllPlayers() throws Exception {
        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldAddNewPlayer() throws Exception {
        String json = """
            {
              "name": "TestPlayer",
              "age": 25,
              "type": "Amatér"
            }
            """;

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnPlayerStats() throws Exception {
        mockMvc.perform(get("/api/players/Novak/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novak"))
                .andExpect(jsonPath("$.matches").value(3))
                .andExpect(jsonPath("$.wins").value(1))
                .andExpect(jsonPath("$.losses").value(2))
                .andExpect(jsonPath("$.winRatePercent").value(33.3));
    }

    // ==========================
// 🔴 NEGATÍVNE SCENÁRE – PlayerController
// ==========================

    @Test
    void shouldFailToAddPlayer_whenNameIsBlank() throws Exception {
        String json = """
      {
        "name": "   ",
        "age": 30,
        "type": "Profesionál"
      }
      """;

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToAddPlayer_whenAgeIsInvalid() throws Exception {
        String json = """
      {
        "name": "BadAge",
        "age": -1,
        "type": "Amatér"
      }
      """;

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailToAddPlayer_whenTypeIsInvalid() throws Exception {
        // ak validuješ typ na zoznam hodnôt (napr. Profesionál/Amatér),
        // tento test spadne na @Valid -> 400
        String json = """
      {
        "name": "TypeX",
        "age": 22,
        "type": "Nezmysel"
      }
      """;

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnStats_forNameCaseInsensitive() throws Exception {
        // overíme case-insensitive správanie (ak ho v StatsService toleruješ)
        mockMvc.perform(get("/api/players/novak/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(equalToIgnoringCase("novak")))
                .andExpect(jsonPath("$.matches").value(3))
                .andExpect(jsonPath("$.wins").value(1))
                .andExpect(jsonPath("$.losses").value(2))
                .andExpect(jsonPath("$.winRatePercent").value(33.3));
    }
}