package sk.peter.tenis.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sk.peter.tenis.model.PlayerType;

import java.io.Serializable;

/**
 * JPA entity representing a tennis player stored in the database.
 *
 * Maps to "players" table and is used in the persistence layer (JPA/Hibernate).
 * Separate from the domain model (Player).
 *
 * Key features:
 * - ID is auto-generated
 * - Name is unique and indexed
 * - Type is stored as enum string
 *
 * Validation:
 * - Name: 2–100 characters, not null
 * - Type: not null
 */
@Entity
@Table(
        name = "players",
        indexes = {
                @Index(name = "idx_player_name", columnList = "name")
        }
)
public class PlayerEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private int age;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlayerType type;

    // --- Constructors ---
    public PlayerEntity() {
    }

    public PlayerEntity(String name, int age, PlayerType type) {
        this.name = name;
        this.age = age;
        this.type = type;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PlayerEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", type=" + type +
                '}';
    }

    // --- equals & hashCode (JPA best practice) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerEntity)) return false;
        PlayerEntity that = (PlayerEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}