package sk.peter.tenis.entity;

import jakarta.persistence.*;
import sk.peter.tenis.model.PlayerType;

/**
 * JPA entity representing a tennis player stored in the database.
 *
 * Maps to the "players" table and contains basic player attributes
 * such as name, age and type.
 */

@Entity
@Table(name = "players")
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private int age;

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

    public void setId(Long id) {
        this.id = id;
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
}