package com.md.backend.service.impl;

import com.md.backend.dto.Invoice.CreateInvoiceRequest;
import com.md.backend.dto.Invoice.InvoiceDto;
import com.md.backend.dto.Item.ItemDto;
import com.md.backend.entity.Invoice;
import com.md.backend.entity.Item;
import com.md.backend.repository.InvoiceRepository;
import com.md.backend.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Creates a new invoice based on the provided request data.
     *
     * @param createInvoiceRequest the request object containing the details of the invoice to be created.
     */
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

    /**
     * Retrieves all invoices with pagination support.
     *
     * @param pageable the pagination information including page number and size.
     * @return a page of InvoiceDto objects representing the invoices.
     */
    @Override
    public Page<InvoiceDto> getAllInvoices(Pageable pageable) {
        Page<Invoice> invoicesPage = invoiceRepository.findAll(pageable);
        return invoicesPage.map(this::mapToDto);
    }

    /**
     * Maps an Invoice entity to an InvoiceDto.
     *
     * @param invoice the Invoice entity to be mapped.
     * @return the corresponding InvoiceDto object.
     */
    private InvoiceDto mapToDto(Invoice invoice) {
        List<ItemDto> itemDtos = invoice.getItems() != null ?
                invoice.getItems().stream().map(this::mapToItemDto).collect(Collectors.toList())
                : List.of();

        return new InvoiceDto(
                invoice.getInvoiceId(),
                invoice.getCustomerName(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                itemDtos,
                invoice.getComment(),
                invoice.getTotalPrice()
        );
    }

    /**
     * Maps an Item entity to an ItemDto.
     *
     * @param item the Item entity to be mapped.
     * @return the corresponding ItemDto object.
     */
    private ItemDto mapToItemDto(Item item) {
        return new ItemDto(item.getItemName(), item.getPrice());
    }
}
