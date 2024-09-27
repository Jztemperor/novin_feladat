package com.md.backend.service;

import com.md.backend.dto.Invoice.CreateInvoiceRequest;
import com.md.backend.dto.Invoice.InvoiceDto;
import com.md.backend.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {
    public void createInvoice(CreateInvoiceRequest createInvoiceRequest);
    public Page<InvoiceDto> getAllInvoices(Pageable pageable);
}
