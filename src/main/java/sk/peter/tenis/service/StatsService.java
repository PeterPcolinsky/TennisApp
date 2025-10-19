package sk.peter.tenis.service;

/**
 * Jednoduché štatistiky pre hráčov/zápasy.
 */
public class StatsService {

    /**
     * Vypočíta výhernosť v % zo zadaného počtu výhier a prehier.
     * Ak sú výhry aj prehry nulové, vracia 0.0.
     */
    public double calcWinRate(int wins, int losses) {
        int finished = wins + losses;
        if (finished == 0) return 0.0;
        return (wins * 100.0) / finished;
    }
}