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
class StatsJpaServiceTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Test
    void shouldCalculateStatsDirectlyFromRepository() {
        // Arrange – dvaja hráči
        PlayerEntity federer = new PlayerEntity();
        federer.setName("Roger Federer");
        federer.setAge(41);
        federer.setType(PlayerType.PROFESIONAL);
        playerRepository.save(federer);

        PlayerEntity nadal = new PlayerEntity();
        nadal.setName("Rafael Nadal");
        nadal.setAge(38);
        nadal.setType(PlayerType.PROFESIONAL);
        playerRepository.save(nadal);

        // Federer wins twice, Nadal wins once
        MatchEntity m1 = new MatchEntity();
        m1.setPlayerA(federer);
        m1.setPlayerB(nadal);
        m1.setResult("6-4, 6-3");
        m1.setDate(LocalDate.of(2025, 5, 12));

        MatchEntity m2 = new MatchEntity();
        m2.setPlayerA(federer);
        m2.setPlayerB(nadal);
        m2.setResult("7-6, 6-4");
        m2.setDate(LocalDate.of(2025, 6, 15));

        MatchEntity m3 = new MatchEntity();
        m3.setPlayerA(nadal);
        m3.setPlayerB(federer);
        m3.setResult("6-3, 6-4");
        m3.setDate(LocalDate.of(2025, 7, 10));

        matchRepository.saveAll(List.of(m1, m2, m3));

        // Act – načítame všetky zápasy Federera
        List<MatchEntity> allMatches = matchRepository.findAll();

        long totalMatches = allMatches.stream()
                .filter(m -> m.getPlayerA().equals(federer) || m.getPlayerB().equals(federer))
                .count();

        long wins = allMatches.stream()
                .filter(m -> {
                    String[] firstSet = m.getResult().split(",")[0].trim().split("-");
                    int a = Integer.parseInt(firstSet[0]);
                    int b = Integer.parseInt(firstSet[1]);
                    boolean aWon = a > b;
                    if (aWon && m.getPlayerA().equals(federer)) return true;
                    return !aWon && m.getPlayerB().equals(federer);
                })
                .count();

        long losses = totalMatches - wins;
        double winRate = (double) wins / totalMatches * 100;

        // Assert
        assertThat(totalMatches).isEqualTo(3);
        assertThat(wins).isEqualTo(2);
        assertThat(losses).isEqualTo(1);
        assertThat(winRate).isBetween(66.0, 67.0);
    }
}