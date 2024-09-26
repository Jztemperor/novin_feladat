package com.md.backend.service;

import com.md.backend.dto.Invoice.CreateInvoiceRequest;
import com.md.backend.entity.Invoice;

public interface InvoiceService {
    public void createInvoice(CreateInvoiceRequest createInvoiceRequest);
}
