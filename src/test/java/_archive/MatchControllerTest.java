package _archive;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled("Replaced by MatchControllerCsvTest – kept only for reference")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ===========================
    // CSV mód (default profily)
    // ===========================
    @Nested
    class CsvMode {

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
                    // ✅ už 201
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
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

    // ===========================
    // JPA mód (profil "h2")
    // ===========================
    @Disabled
    @Nested
    @ActiveProfiles("h2")
    class JpaMode {

        @Test
        void shouldCreateThenUpdateThenDeleteById_inJpaMode() throws Exception {
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
                    // ✅ teraz 201 Created
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            // 2) GET ALL
            String listJson = mockMvc.perform(get("/api/matches"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Long matchId = extractMatchId(listJson, "Peter", "Miroslav", "6:4, 6:4", "2025-06-01");
            assertThat(matchId).isNotNull();

            // 3) UPDATE
            String updateJson = """
                    { "newScore": "7:6, 6:4", "newDate": "2025-06-02" }
                    """;

            mockMvc.perform(put("/api/matches/{id}", matchId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            // 4) DELETE
            mockMvc.perform(delete("/api/matches/{id}", matchId))
                    // ✅ teraz 204 No Content
                    .andExpect(status().isNoContent());
        }

        // =======================
        // pomocné metódy
        // =======================
        private Long extractMatchId(String listJson,
                                    String a, String b, String score, String date) {
            try {
                List<Map<String, Object>> items = JsonPath.read(listJson, "$[*]");
                for (Map<String, Object> item : items) {
                    String aName = tryGetNestedName(item, "playerA");
                    String bName = tryGetNestedName(item, "playerB");
                    String sc = stringOrNull(item.get("score"));
                    String dt = stringOrNull(item.get("date"));
                    if (aName == null) aName = stringOrNull(item.get("playerAName"));
                    if (bName == null) bName = stringOrNull(item.get("playerBName"));

                    if (equalsIgnore(aName, a) && equalsIgnore(bName, b)
                            && equalsIgnore(sc, score) && equalsIgnore(dt, date)) {
                        Object idVal = item.get("id");
                        if (idVal instanceof Number n) return n.longValue();
                        if (idVal instanceof String s) return Long.parseLong(s);
                    }
                }
            } catch (Exception ignored) {
            }
            return null;
        }

        private String tryGetNestedName(Map<String, Object> root, String key) {
            try {
                Object nested = root.get(key);
                if (nested instanceof Map<?, ?> map) {
                    Object name = map.get("name");
                    return name != null ? name.toString() : null;
                }
            } catch (Exception ignored) {
            }
            return null;
        }

        private String stringOrNull(Object o) {
            return o == null ? null : o.toString();
        }

        private boolean equalsIgnore(String a, String b) {
            return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
        }
    }
}