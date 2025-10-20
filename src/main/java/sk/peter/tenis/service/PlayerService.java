package sk.peter.tenis.service;

import org.springframework.stereotype.Service;
import sk.peter.tenis.dto.PlayerDto;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.model.PlayerType;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerService {

    public List<Player> findAll() {
        List<Player> result = new ArrayList<>();
        try {
            CsvService.loadPlayers(result);
        } catch (Exception e) {
            // CSV fáza: ticho vrátime to, čo je
        }
        return result;
    }

    public Player createFromDto(PlayerDto dto) {
        // 1) namapuj typ – skús najprv tvoju utilitu, potom názov enumu
        PlayerType type = PlayerType.fromInput(dto.getType());
        if (type == null) {
            try {
                type = PlayerType.valueOf(dto.getType().trim().toUpperCase());
            } catch (Exception ignored) {
                type = PlayerType.AMATER; // fallback
            }
        }

        // 2) vytvor Player cez KONŠTRUKTOR (tvoj poriadok je name, age, type)
        Player p = new Player(dto.getName().trim(), dto.getAge(), type);

        // 3) načítaj, pridaj, ulož
        List<Player> players = new ArrayList<>();
        try {
            CsvService.loadPlayers(players);
            players.add(p);
            CsvService.savePlayers(players);
        } catch (Exception ignored) {
        }
        return p;
    }
}