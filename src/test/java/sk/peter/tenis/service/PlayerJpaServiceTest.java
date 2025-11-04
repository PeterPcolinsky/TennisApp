package sk.peter.tenis.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sk.peter.tenis.entity.PlayerEntity;
import sk.peter.tenis.model.PlayerType;
import sk.peter.tenis.repository.PlayerRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PlayerJpaServiceTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void shouldCreateAndFindPlayer() {
        // Arrange
        PlayerEntity player = new PlayerEntity();
        player.setName("Roger Federer");
        player.setAge(41);
        player.setType(PlayerType.PROFESIONAL);

        // Act
        playerRepository.save(player);

        // Assert
        List<PlayerEntity> players = playerRepository.findAll();
        assertThat(players).hasSize(1);
        assertThat(players.get(0).getName()).isEqualTo("Roger Federer");
    }

    @Test
    void shouldUpdatePlayer() {
        // Arrange
        PlayerEntity player = new PlayerEntity();
        player.setName("Novak Djokovic");
        player.setAge(37);
        player.setType(PlayerType.PROFESIONAL);
        playerRepository.save(player);

        // Act
        PlayerEntity existing = playerRepository.findAll().get(0);
        existing.setAge(38);
        playerRepository.save(existing);

        // Assert
        PlayerEntity updated = playerRepository.findById(existing.getId()).orElseThrow();
        assertThat(updated.getAge()).isEqualTo(38);
    }

    @Test
    void shouldDeletePlayer() {
        // Arrange
        PlayerEntity player = new PlayerEntity();
        player.setName("Rafael Nadal");
        player.setAge(38);
        player.setType(PlayerType.PROFESIONAL);
        playerRepository.save(player);

        List<PlayerEntity> before = playerRepository.findAll();
        assertThat(before).hasSize(1);

        // Act
        playerRepository.deleteById(player.getId());

        // Assert
        List<PlayerEntity> after = playerRepository.findAll();
        assertThat(after).isEmpty();
    }
}