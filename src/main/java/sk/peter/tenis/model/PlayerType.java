package sk.peter.tenis.model;

public enum PlayerType {
    AMATER("Amatér"),
    PROFESIONAL("Profesionál");

    private final String displayName;

    PlayerType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Pomocná metóda na načítanie podľa vstupu používateľa
    public static PlayerType fromInput(String input) {
        String normalized = input.trim().toLowerCase();
        return switch (normalized) {
            case "amater", "amatér" -> AMATER;
            case "profesional", "profesionál" -> PROFESIONAL;
            default -> null;
        };
    }
}