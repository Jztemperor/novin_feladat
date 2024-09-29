package com.md.backend.dto.ApplicationUser;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateUserRequest {
    @NotNull(message = "A felhasználó azonosító kötelező!")
    private Long userId;
    @NotNull(message = "A szerekpör azonosítók megadása kötelező!")
    private List<Long> roleIds;
}
