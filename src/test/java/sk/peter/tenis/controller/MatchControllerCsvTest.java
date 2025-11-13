package sk.peter.tenis.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sk.peter.tenis.annotations.TestWithoutSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestWithoutSecurity
@ActiveProfiles("h2")
// 💡 bez @ActiveProfiles – beží v "default" → CSV logika
class MatchControllerCsvTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldListMatches() throws Exception {
        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldCreateMatch_returns201_inCsvMode() throws Exception {
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
    }

    @Test
    void shouldFailCreate_whenPlayerMissing_inCsvMode() throws Exception {
        String createJson = """
                {
                  "playerA": "Neznamy",
                  "playerB": "Peter",
                  "score": "6:4, 6:4",
                  "date": "2025-06-10"
                }
                """;

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectInvalidDateFormat_onDtoValidation_inCsvMode() throws Exception {
        String badDateJson = """
                {
                  "playerA": "Peter",
                  "playerB": "Miroslav",
                  "score": "6:4, 6:4",
                  "date": "01-06-2025"
                }
                """;

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badDateJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_onUpdateById_inCsvMode() throws Exception {
        String updateJson = """
                { "newScore": "7:6, 6:4", "newDate": "2025-06-02" }
                """;

        mockMvc.perform(put("/api/matches/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_onDeleteById_inCsvMode() throws Exception {
        mockMvc.perform(delete("/api/matches/1"))
                .andExpect(status().isBadRequest());
    }
}