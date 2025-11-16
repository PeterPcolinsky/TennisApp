package sk.peter.tenis.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sk.peter.tenis.annotations.TestWithoutSecurity;
import sk.peter.tenis.entity.MatchEntity;
import sk.peter.tenis.entity.PlayerEntity;
import sk.peter.tenis.model.PlayerType;
import sk.peter.tenis.repository.MatchRepository;
import sk.peter.tenis.repository.PlayerRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestWithoutSecurity
@ActiveProfiles("h2")
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @BeforeEach
    void setup() {

        matchRepository.deleteAll();
        playerRepository.deleteAll();

        // === Players (CSV) ===
        PlayerEntity peter = playerRepository.save(new PlayerEntity("Peter", 37, PlayerType.PROFESIONAL));
        PlayerEntity miroslav = playerRepository.save(new PlayerEntity("Miroslav", 39, PlayerType.AMATER));
        PlayerEntity novak = playerRepository.save(new PlayerEntity("Novak", 43, PlayerType.PROFESIONAL));
        PlayerEntity roger = playerRepository.save(new PlayerEntity("Roger", 44, PlayerType.PROFESIONAL));
        PlayerEntity rafael = playerRepository.save(new PlayerEntity("Rafael", 38, PlayerType.PROFESIONAL));
        PlayerEntity skuska = playerRepository.save(new PlayerEntity("Skuska", 99, PlayerType.PROFESIONAL));

        // === Matches (CSV) ===
        matchRepository.save(new MatchEntity(peter, novak, "6:4, 6:2, 6:2", LocalDate.of(2025, 5, 10)));
        matchRepository.save(new MatchEntity(peter, miroslav, "7:6, 6:0, 6:0", LocalDate.of(2025, 4, 13)));
        matchRepository.save(new MatchEntity(miroslav, novak, "0:6, 0:6, 0:6", LocalDate.of(2025, 1, 8)));
        matchRepository.save(new MatchEntity(novak, roger, "6:7, 6:7, 6:7", LocalDate.of(2025, 5, 5)));
        matchRepository.save(new MatchEntity(peter, rafael, "6:3, 6:4", LocalDate.of(2025, 6, 1)));
        matchRepository.save(new MatchEntity(peter, miroslav, "6:4, 6:4", LocalDate.of(2025, 6, 1)));
    }

    @Test
    void shouldReturnLeaderboard_sortedByWinRate() throws Exception {
        mockMvc.perform(get("/api/stats/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].winRatePercent", is(100.0)))
                .andExpect(jsonPath("$[1].winRatePercent", is(100.0)))
                .andExpect(jsonPath("$[0].name", anyOf(is("Peter"), is("Roger"))))
                .andExpect(jsonPath("$[1].name", anyOf(is("Peter"), is("Roger"))))
                .andExpect(jsonPath("$[2].name", is("Novak")))
                .andExpect(jsonPath("$[2].winRatePercent", is(33.3)));
    }

    @Test
    void shouldExportLeaderboardCsv() throws Exception {
        mockMvc.perform(get("/api/stats/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("leaderboard.csv")))
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(content().string(containsString("Meno;Zapasy;Vyhry;Prehry;WinRate(%)")))
                .andExpect(content().string(containsString("Peter;4;4;0;100.0")))
                .andExpect(content().string(containsString("Novak;3;1;2;33.3")));
    }

    @Test
    void shouldReturnTopNPlayers() throws Exception {
        mockMvc.perform(get("/api/stats/top").param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].winRatePercent", is(100.0)));
    }

    @Test
    void shouldReturnPlayerStatsWithDateRange() throws Exception {
        mockMvc.perform(get("/api/stats/player")
                        .param("name", "Peter")
                        .param("from", "2025-04-01")
                        .param("to", "2025-05-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Peter")))
                .andExpect(jsonPath("$.matches", is(2)))
                .andExpect(jsonPath("$.wins", is(2)))
                .andExpect(jsonPath("$.losses", is(0)))
                .andExpect(jsonPath("$.winRatePercent", is(100.0)));
    }
}