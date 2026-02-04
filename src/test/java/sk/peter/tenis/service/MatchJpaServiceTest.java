package sk.peter.tenis.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sk.peter.tenis.entity.MatchEntity;
import sk.peter.tenis.entity.PlayerEntity;
import sk.peter.tenis.model.PlayerType;
import sk.peter.tenis.repository.MatchRepository;
import sk.peter.tenis.repository.PlayerRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MatchJpaServiceTest {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void shouldCreateAndFindMatch() {
        // Arrange – vytvoríme dvoch hráčov
        PlayerEntity playerA = new PlayerEntity();
        playerA.setName("Roger Federer");
        playerA.setAge(41);
        playerA.setType(PlayerType.PROFESIONAL);
        playerRepository.save(playerA);

        PlayerEntity playerB = new PlayerEntity();
        playerB.setName("Rafael Nadal");
        playerB.setAge(38);
        playerB.setType(PlayerType.PROFESIONAL);
        playerRepository.save(playerB);

        // Act – vytvoríme zápas
        MatchEntity match = new MatchEntity();
        match.setPlayerA(playerA);
        match.setPlayerB(playerB);
        match.setResult("6-4, 7-6");
        match.setDate(LocalDate.of(2025, 5, 12));

        matchRepository.save(match);

        // Assert – skontrolujeme uloženie
        List<MatchEntity> matches = matchRepository.findAll();
        assertThat(matches).hasSize(1);
        assertThat(matches.get(0).getResult()).isEqualTo("6-4, 7-6");
    }

    @Test
    void shouldDeleteMatch() {
        // Arrange – dvoch hráčov
        PlayerEntity playerA = new PlayerEntity();
        playerA.setName("Novak Djokovic");
        playerA.setAge(37);
        playerA.setType(PlayerType.PROFESIONAL);
        playerRepository.save(playerA);

        PlayerEntity playerB = new PlayerEntity();
        playerB.setName("Carlos Alcaraz");
        playerB.setAge(22);
        playerB.setType(PlayerType.PROFESIONAL);
        playerRepository.save(playerB);

        // Zápas
        MatchEntity match = new MatchEntity();
        match.setPlayerA(playerA);
        match.setPlayerB(playerB);
        match.setResult("6-1, 6-2");
        match.setDate(LocalDate.of(2025, 6, 1));
        matchRepository.save(match);

        List<MatchEntity> before = matchRepository.findAll();
        assertThat(before).hasSize(1);

        // Act
        matchRepository.deleteById(match.getId());

        // Assert
        List<MatchEntity> after = matchRepository.findAll();
        assertThat(after).isEmpty();
    }
}