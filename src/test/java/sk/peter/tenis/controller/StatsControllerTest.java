package sk.peter.tenis.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnLeaderboard_sortedByWinRate() throws Exception {
        mockMvc.perform(get("/api/stats/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // prvý je Peter (100%), druhý Roger (100%), potom Novak (33.3) – podľa tvojich CSV
                .andExpect(jsonPath("$[0].name", anyOf(is("Peter"), is("Roger"))))
                .andExpect(jsonPath("$[0].winRatePercent", is(100.0)))
                .andExpect(jsonPath("$[1].name", anyOf(is("Peter"), is("Roger"))))
                .andExpect(jsonPath("$[1].winRatePercent", is(100.0)))
                .andExpect(jsonPath("$[2].name", is("Novak")))
                .andExpect(jsonPath("$[2].winRatePercent", is(33.3)));
    }

    @Test
    void shouldExportLeaderboardCsv() throws Exception {
        mockMvc.perform(get("/api/stats/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("leaderboard.csv")))
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                // hlavička
                .andExpect(content().string(containsString("Meno;Zapasy;Vyhry;Prehry;WinRate(%)")))
                // typické riadky z tvojich dát
                .andExpect(content().string(containsString("Peter;2;2;0;100.0")))
                .andExpect(content().string(containsString("Novak;3;1;2;33.3")));
    }
}