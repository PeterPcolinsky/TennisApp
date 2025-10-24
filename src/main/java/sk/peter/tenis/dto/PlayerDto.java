package sk.peter.tenis.dto;

import jakarta.validation.constraints.*;

public class PlayerDto {
    @NotBlank(message = "Meno je povinné")
    @Size(max = 50, message = "Meno môže mať max 50 znakov")
    private String name;

    @Min(value = 5, message = "Vek musí byť aspoň 5")
    @Max(value = 120, message = "Vek musí byť najviac 120")
    private int age;

    @NotBlank(message = "Typ je povinný (AMATER alebo PROFESIONAL)")
    @Pattern(
            regexp = "(?i)(AMATER|PROFESIONAL|Amatér|Profesionál)",
            message = "Typ musí byť AMATER alebo PROFESIONAL (bez ohľadu na veľkosť písmen)"
    )
    private String type; // prijímame ako text, mapneme na enum

    // gettre/settre
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