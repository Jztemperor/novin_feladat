package com.md.backend.dto.ApplicationUser;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NotNull(message = "A felhasználó azonosító kötelező!")
    private Long userId;
    @NotNull(message = "A szerekpör azonosítók megadása kötelező!")
    private List<Long> roleIds;
}
