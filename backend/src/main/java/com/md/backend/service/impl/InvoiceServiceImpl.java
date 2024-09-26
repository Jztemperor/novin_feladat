package com.md.backend.service.impl;

import com.md.backend.dto.Invoice.CreateInvoiceRequest;
import com.md.backend.dto.Item.ItemDto;
import com.md.backend.entity.Invoice;
import com.md.backend.entity.Item;
import com.md.backend.repository.InvoiceRepository;
import com.md.backend.service.InvoiceService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public void createInvoice(CreateInvoiceRequest createInvoiceRequest) {

        // Determine total price
        double total = createInvoiceRequest.getItems().stream()
                .mapToDouble(ItemDto::getPrice)
                .sum();

        Invoice invoice = new Invoice();
        invoice.setCustomerName(createInvoiceRequest.getCustomerName());
        invoice.setIssueDate(createInvoiceRequest.getIssueDate());
        invoice.setDueDate(createInvoiceRequest.getDueDate());
        invoice.setTotalPrice(total);
        invoice.setComment(createInvoiceRequest.getComment());

        List<Item> items = createInvoiceRequest.getItems().stream()
                        .map(itemDto -> {
                            Item item = new Item();
                            item.setItemName(itemDto.getItemName());
                            item.setPrice(itemDto.getPrice());
                            item.setInvoice(invoice);
                            return item;
                        })
                        .toList();

        invoice.setItems(items);
        invoiceRepository.save(invoice);
    }
}
