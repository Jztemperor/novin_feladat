package com.md.backend.dto.Authentication;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    @NotEmpty(message = "A név megadása kötelező!")
    private String name;

    @NotEmpty(message = "A felhasználónév megadása kötelező!")
    private String username;

    @NotEmpty(message = "A jelszó megadása kötelező!")
    private String password;

    @NotEmpty(message = "A szerepkör kiválasztása kötelező!")
    private String roleName;
}
