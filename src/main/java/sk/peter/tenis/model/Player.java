package sk.peter.tenis.model;

import java.util.Objects;

public class Player {
    private String name;
    private int age;
    private PlayerType type;

    /**
     * Doménový objekt hráča: meno, vek a typ (amatér/profesionál).
     */
    public Player(String name, int age, PlayerType type) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be greater than 0");
        }
        if (type == null) {
            throw new IllegalArgumentException("Player type must not be null");
        }

        this.name = name.trim();
        this.age = age;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public PlayerType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Hráč: " + name + " (" + age + " r.) - " + type.getDisplayName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return age == player.age &&
                Objects.equals(name, player.name) &&
                type == player.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, type);
    }
}