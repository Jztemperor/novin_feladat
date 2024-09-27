package com.md.backend.dto.Item;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    @NotEmpty(message = "A tétel név megadása kötelező!")
    private String itemName;

    @NotEmpty(message = "A tétel árának megadása kötelező!")
    private double price;
}
