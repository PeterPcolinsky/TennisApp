package sk.peter.tenis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.peter.tenis.entity.MatchEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for working with match entities.
 *
 * <p>Provides basic CRUD operations inherited from {@link JpaRepository}
 * and custom query methods for filtering and duplicate checks.</p>
 */
public interface MatchRepository extends JpaRepository<MatchEntity, Long> {

    /**
     * Returns matches played within the given date range.
     *
     * @param from start date
     * @param to end date
     * @return list of matches between the given dates
     */
    List<MatchEntity> findByDateBetween(LocalDate from, LocalDate to);

    /**
     * Returns matches where the given player appears either as player A or player B.
     *
     * @param nameA player name for player A comparison
     * @param nameB player name for player B comparison
     * @return list of matches for the given player
     */
    List<MatchEntity> findByPlayerA_NameIgnoreCaseOrPlayerB_NameIgnoreCase(String nameA, String nameB);

    /**
     * Searches matches using optional player name and optional date range filters.
     *
     * @param name player name, or {@code null} to ignore player filter
     * @param from start date, or {@code null} to ignore lower date bound
     * @param to end date, or {@code null} to ignore upper date bound
     * @return filtered list of matches sorted by date ascending
     */
    @Query("""
            select m from MatchEntity m
            where (:from is null or m.date >= :from)
              and (:to   is null or m.date <= :to)
              and (
                    :name is null
                    or lower(m.playerA.name) = lower(:name)
                    or lower(m.playerB.name) = lower(:name)
                  )
            order by m.date asc
            """)
    List<MatchEntity> search(
            @Param("name") String name,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    /**
     * Checks whether a match already exists for the given players and date.
     *
     * @param playerA name of player A
     * @param playerB name of player B
     * @param date match date
     * @return {@code true} if such match exists
     */
    boolean existsByPlayerA_NameIgnoreCaseAndPlayerB_NameIgnoreCaseAndDate(String playerA,
                                                                           String playerB,
                                                                           LocalDate date);

    /**
     * Checks whether a match already exists for the given players in reversed order and date.
     *
     * @param playerB name of player B
     * @param playerA name of player A
     * @param date match date
     * @return {@code true} if such match exists
     */
    boolean existsByPlayerB_NameIgnoreCaseAndPlayerA_NameIgnoreCaseAndDate(String playerB,
                                                                           String playerA,
                                                                           LocalDate date);
}