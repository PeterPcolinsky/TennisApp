package sk.peter.tenis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.peter.tenis.entity.MatchEntity;

import java.time.LocalDate;
import java.util.List;

public interface MatchRepository extends JpaRepository<MatchEntity, Long> {

    List<MatchEntity> findByDateBetween(LocalDate from, LocalDate to);

    List<MatchEntity> findByPlayerA_NameIgnoreCaseOrPlayerB_NameIgnoreCase(String nameA, String nameB);

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

    // --- Duplicate checks (A vs B in either order on the same date) ---
    boolean existsByPlayerA_NameIgnoreCaseAndPlayerB_NameIgnoreCaseAndDate(String playerA,
                                                                           String playerB,
                                                                           LocalDate date);

    boolean existsByPlayerB_NameIgnoreCaseAndPlayerA_NameIgnoreCaseAndDate(String playerB,
                                                                           String playerA,
                                                                           LocalDate date);
}