package com.md.backend.dto.Item;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ItemDto {
    @NotEmpty(message = "A tétel név megadása kötelező!")
    private String itemName;

    @NotEmpty(message = "A tétel árának megadása kötelező!")
    private double price;
}
