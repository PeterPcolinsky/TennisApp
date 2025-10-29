package sk.peter.tenis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.peter.tenis.entity.PlayerEntity;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

    Optional<PlayerEntity> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
