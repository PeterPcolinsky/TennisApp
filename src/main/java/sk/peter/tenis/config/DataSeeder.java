package sk.peter.tenis.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import sk.peter.tenis.entity.PlayerEntity;
import sk.peter.tenis.model.PlayerType;
import sk.peter.tenis.repository.PlayerRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Jednorazové nahratie hráčov z CSV do H2 databázy.
 * Spúšťa sa iba s profilom 'h2' a iba keď je tabuľka players prázdna.
 */
@Configuration
@Profile("h2")
public class DataSeeder {

    private final PlayerRepository playerRepository;

    // cestu berieme application.properties
    @Value("${tenis.csv.players:./data/players.csv}")
    private String playersCsvPath;

    public DataSeeder(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    @PostConstruct
    public void seed() throws IOException {
        if (playerRepository.count() > 0) {
            return; // databáza už má dáta -> neseedujeme znova
        }

        Path path = Path.of(playersCsvPath);
        if (!Files.exists(path)) {
            System.out.println("⚠ players.csv nenájdený na ceste: " + path.toAbsolutePath());
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                // očakávaný formát: Meno;Vek;Typ   (napr. "Peter;30;Amatér")
                String[] parts = line.split(";");
                if (parts.length < 3) continue;

                String name = parts[0].trim();
                int age;
                try {
                    age = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException ex) {
                    continue; // preskoč riadok s neplatným vekom
                }
                PlayerType type = PlayerType.fromInput(parts[2].trim());
                if (type == null) {
                    // pokus o fallback: skús bez diakritiky/uppercase
                    String t = parts[2].trim().toLowerCase();
                    if (t.startsWith("amat")) type = PlayerType.AMATER;
                    else if (t.startsWith("prof")) type = PlayerType.PROFESIONAL;
                    else continue;
                }

                // vyhnutie sa duplicitám podľa mena (case-insensitive)
                if (playerRepository.existsByNameIgnoreCase(name)) continue;

                PlayerEntity entity = new PlayerEntity(name, age, type);
                playerRepository.save(entity);
            }
        }
    }
}