package sk.peter.tenis.model;

public class Player {
    private String name;
    private int age;
    private PlayerType type;

    /**
     * Doménový objekt hráča: meno, vek a typ (amatér/profesionál).
     */

    public Player(String name, int age, PlayerType type) {
        this.name = name;
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
}
