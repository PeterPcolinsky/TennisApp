package sk.peter.tenis.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sk.peter.tenis.model.Match;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.service.CsvService;
import sk.peter.tenis.service.jpa.MatchJpaService;
import sk.peter.tenis.service.jpa.PlayerJpaService;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("mysql")   // 💡 Aktivuje sa iba pri MySQL profile
public class MySqlDataSeeder {

    private final PlayerJpaService playerJpaService;
    private final MatchJpaService matchJpaService;

    public MySqlDataSeeder(PlayerJpaService playerJpaService,
                           MatchJpaService matchJpaService) {
        this.playerJpaService = playerJpaService;
        this.matchJpaService = matchJpaService;
    }

    @Transactional
    @PostConstruct
    public void seedData() {
        try {
            // 🚫 Ak databáza už obsahuje dáta, neimportuj znova
            if (!playerJpaService.findAll().isEmpty() || !matchJpaService.findAll().isEmpty()) {
                System.out.println("⚠️ Dáta už existujú v MySQL – import z CSV preskočený.");
                return;
            }

            // ✅ Načítaj hráčov z CSV
            List<Player> players = new ArrayList<>();
            CsvService.loadPlayers(players);
            if (!players.isEmpty()) {
                players.forEach(playerJpaService::save);
                System.out.println("✅ Naimportovaných hráčov: " + players.size());
            }

            // ✅ Načítaj zápasy z CSV
            List<Match> matches = new ArrayList<>();
            CsvService.loadMatches(matches, players);
            if (!matches.isEmpty()) {
                matches.forEach(matchJpaService::save);
                System.out.println("✅ Naimportovaných zápasov: " + matches.size());
            }

            if (!players.isEmpty() || !matches.isEmpty()) {
                System.out.println("🎾 CSV dáta boli úspešne naimportované do MySQL.");
            }

        } catch (Exception e) {
            System.err.println("⚠️ Chyba pri importe CSV do MySQL: " + e.getMessage());
        }
    }
}