package sk.peter.tenis.model;

/**
 * Player type (AMATEUR / PROFESSIONAL) including mapping from user input.
 */
public enum PlayerType {
    AMATER("Amatér"),
    PROFESIONAL("Profesionál");

    private final String displayName;

    PlayerType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return display name of the player type
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converts user input string to PlayerType.
     *
     * @param input user input (e.g. "amater", "amatér", "profesionál")
     * @return corresponding PlayerType
     * @throws IllegalArgumentException if input is invalid
     */
    public static PlayerType fromInput(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Player type cannot be null");
        }

        String normalized = input.trim().toLowerCase();

        return switch (normalized) {
            case "amater", "amatér" -> AMATER;
            case "profesional", "profesionál" -> PROFESIONAL;
            default -> throw new IllegalArgumentException("Unknown player type: " + input);
        };
    }
}