package com.md.backend.enums;

public enum RoleNamesEnum {
    KONYVELO("Konyvelo"),
    ADMINISZTRATOR("Adminisztrator"),
    FELHASZNALO("Felhasznalo");

    private final String name;

    RoleNamesEnum(String displayName) {
        this.name = displayName;
    }

    public String getName() {
        return name;
    }
}
