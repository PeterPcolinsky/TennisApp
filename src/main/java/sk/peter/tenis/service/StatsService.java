package sk.peter.tenis.service;

public class StatsService {
    // jednoducha sluzba na statistiky - bude sa rozsirovat
    public double calcWinRate(int wins, int losses) {
        int total = wins + losses;
        if (total == 0) return 0.0;
        return (wins * 100.0) / total;
    }
}
