package com.md.backend.dto.Invoice;

import com.md.backend.dto.Item.ItemDto;
import com.md.backend.entity.Item;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateInvoiceRequest {

    @NotEmpty(message = "A vásárló név megadása kötelező!")
    private String customerName;

    @NotNull(message = "A kiállítási dátum megadása kötelező!")
    private LocalDate issueDate;

    @NotNull(message = "Az esedékesség dátum megadása kötelező!")
    private LocalDate dueDate;

    @NotEmpty(message = "A tétel(ek) megadása kötelező!")
    private List<ItemDto> items;

    @NotEmpty(message = "A komment megadása kötelező")
    private String comment;
}
