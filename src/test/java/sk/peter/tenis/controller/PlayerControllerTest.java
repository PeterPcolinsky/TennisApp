package sk.peter.tenis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sk.peter.tenis.annotations.TestWithoutSecurity;
import sk.peter.tenis.dto.PlayerDto;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.model.PlayerType;
import sk.peter.tenis.service.PlayerService;
import sk.peter.tenis.service.jpa.PlayerJpaService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PlayerController.class)
@TestWithoutSecurity
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private PlayerJpaService playerJpaService;  // ⬅️ DOPLNENÉ mock pre JPA service

    @Autowired
    private ObjectMapper objectMapper;

    private PlayerDto player1;
    private PlayerDto player2;

    @BeforeEach
    void setup() {
        player1 = new PlayerDto();
        player1.setName("Roger Federer");
        player1.setAge(40);
        player1.setType("PROFESIONAL");

        player2 = new PlayerDto();
        player2.setName("Rafael Nadal");
        player2.setAge(38);
        player2.setType("PROFESIONAL");
    }

    @Test
    void shouldReturnAllPlayers() throws Exception {
        given(playerService.findAll()).willReturn(List.of(
                new Player("Roger Federer", 40, PlayerType.PROFESIONAL),
                new Player("Rafael Nadal", 38, PlayerType.PROFESIONAL)
        ));

        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Roger Federer", "Rafael Nadal")));
    }

    @Test
    void shouldCreatePlayer() throws Exception {
        given(playerService.createFromDto(any(PlayerDto.class)))
                .willReturn(new Player("Roger Federer", 40, PlayerType.PROFESIONAL));

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Roger Federer")))
                .andExpect(jsonPath("$.age", is(40)))
                .andExpect(jsonPath("$.type", is("PROFESIONAL")));
    }

    @Test
    void shouldUpdatePlayer() throws Exception {
        given(playerService.update(eq("Roger Federer"), any(PlayerDto.class)))
                .willReturn(new Player("Roger Federer", 41, PlayerType.AMATER));

        PlayerDto updated = new PlayerDto();
        updated.setName("Roger Federer");
        updated.setAge(41);
        updated.setType("AMATER");

        mockMvc.perform(put("/api/players/{name}", "Roger Federer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age", is(41)))
                .andExpect(jsonPath("$.type", is("AMATER")));
    }

    @Test
    void shouldDeletePlayer() throws Exception {
        doNothing().when(playerService).delete("Roger Federer");

        mockMvc.perform(delete("/api/players/{name}", "Roger Federer"))
                .andExpect(status().isNoContent());
    }
}