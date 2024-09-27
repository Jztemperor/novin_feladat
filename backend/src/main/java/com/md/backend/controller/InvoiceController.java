package com.md.backend.controller;

import com.md.backend.dto.Invoice.CreateInvoiceRequest;
import com.md.backend.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createInvoice(@Valid @RequestBody CreateInvoiceRequest invoice) {
        invoiceService.createInvoice(invoice);
        return ResponseEntity.ok("Invoice created!");
    }
}
