package com.md.backend.dto.Invoice;

import com.md.backend.dto.Item.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDto {
    private Long id;
    private String customerName;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private List<ItemDto> items;
    private String comment;
    private double totalPrice;
}
