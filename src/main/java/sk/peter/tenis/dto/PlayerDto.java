package sk.peter.tenis.dto;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object (DTO) representing a player.
 *
 * Used for receiving input data from API requests (e.g. creating a player).
 * Contains validation constraints for incoming data.
 */
public class PlayerDto {

    /**
     * Player's name.
     * Required field, maximum length 50 characters, letters and spaces only.
     */
    @NotBlank(message = "Meno je povinné")
    @Size(max = 50, message = "Meno môže mať max 50 znakov")
    @Pattern(
            regexp = "^[A-Za-zÀ-ž]+(?: [A-Za-zÀ-ž]+)*$",
            message = "Meno môže obsahovať len písmená a medzery (bez číslic a špeciálnych znakov)"
    )
    private String name;

    /**
     * Player's age.
     * Allowed range: 5 - 120 years.
     */
    @Min(value = 5, message = "Vek musí byť aspoň 5")
    @Max(value = 120, message = "Vek musí byť najviac 120")
    private int age;

    /**
     * Player type (AMATEUR or PROFESSIONAL).
     * Accepted as a String and later mapped to enum.
     */
    @NotBlank(message = "Typ je povinný (AMATER alebo PROFESIONAL)")
    @Pattern(
            regexp = "(?i)(AMATER|PROFESIONAL|Amatér|Profesionál)",
            message = "Typ musí byť AMATER alebo PROFESIONAL (bez ohľadu na veľkosť písmen)"
    )
    private String type;

    // getters / setters
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}