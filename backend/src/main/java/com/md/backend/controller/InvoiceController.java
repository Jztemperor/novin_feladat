package com.md.backend.controller;

import com.md.backend.dto.Invoice.CreateInvoiceRequest;
import com.md.backend.dto.Invoice.InvoiceDto;
import com.md.backend.entity.Invoice;
import com.md.backend.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public ResponseEntity<Page<InvoiceDto>> getInvoices(Pageable pageable) {
        return new ResponseEntity<>(invoiceService.getAllInvoices(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> getInvoice(@PathVariable Long id) {
        return new ResponseEntity<>(invoiceService.getInvoice(id), HttpStatus.FOUND);
    }
}
