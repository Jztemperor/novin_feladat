package com.md.backend.service;

import com.md.backend.dto.Invoice.CreateInvoiceRequest;
import com.md.backend.dto.Invoice.InvoiceDto;
import com.md.backend.dto.Item.ItemDto;
import com.md.backend.entity.Invoice;
import com.md.backend.entity.Item;
import com.md.backend.repository.InvoiceRepository;
import com.md.backend.service.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Test
    public void createInvoice_ShouldSaveInvoiceWithItems() {
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setCustomerName("John Doe");
        request.setIssueDate(LocalDate.of(2024, 9, 26));
        request.setDueDate(LocalDate.of(2024, 10, 26));
        request.setComment("Sample invoice");

        ItemDto item1 = new ItemDto();
        item1.setItemName("Item 1");
        item1.setPrice(100.0);

        ItemDto item2 = new ItemDto();
        item2.setItemName("Item 2");
        item2.setPrice(200.0);

        request.setItems(Arrays.asList(item1, item2));

        // Act
        invoiceService.createInvoice(request);

        // Assert
        ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(invoiceCaptor.capture());

        Invoice savedInvoice = invoiceCaptor.getValue();
        assertEquals("John Doe", savedInvoice.getCustomerName());
        assertEquals(LocalDate.of(2024, 9, 26), savedInvoice.getIssueDate());
        assertEquals(LocalDate.of(2024, 10, 26), savedInvoice.getDueDate());
        assertEquals("Sample invoice", savedInvoice.getComment());
        assertEquals(300.0, savedInvoice.getTotalPrice());

        List<Item> savedItems = savedInvoice.getItems();
        assertEquals("Item 1", savedItems.get(0).getItemName());
        assertEquals(100.0, savedItems.get(0).getPrice());
        assertEquals(savedInvoice, savedItems.get(0).getInvoice());
        assertEquals("Item 2", savedItems.get(1).getItemName());
        assertEquals(200.0, savedItems.get(1).getPrice());
        assertEquals(savedInvoice, savedItems.get(1).getInvoice());
    }

    @Test
    public void getAllInvoices_ReturnsAllInvoices() {
        Invoice invoice1 = new Invoice();
        invoice1.setInvoiceId(1L);
        invoice1.setCustomerName("Customer A");
        invoice1.setIssueDate(LocalDate.now());
        invoice1.setDueDate(LocalDate.now().plusDays(30));
        invoice1.setComment("First Invoice");
        invoice1.setTotalPrice(100.0);

        Invoice invoice2 = new Invoice();
        invoice2.setInvoiceId(2L);
        invoice2.setCustomerName("Customer B");
        invoice2.setIssueDate(LocalDate.now());
        invoice2.setDueDate(LocalDate.now().plusDays(30));
        invoice2.setComment("Second Invoice");
        invoice2.setTotalPrice(200.0);

        List<Invoice> invoices = Arrays.asList(invoice1, invoice2);
        Page<Invoice> invoicePage = new PageImpl<>(invoices);

        Pageable pageable = PageRequest.of(0, 10);
        when(invoiceRepository.findAll(pageable)).thenReturn(invoicePage);

        // act
        Page<InvoiceDto> result = invoiceService.getAllInvoices(pageable);

        // Assert
        assertEquals(2, result.getTotalElements());
        assertEquals("Customer A", result.getContent().get(0).getCustomerName());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals("Customer B", result.getContent().get(1).getCustomerName());
        assertEquals(2L, result.getContent().get(1).getId());

        // Verify the interaction
        verify(invoiceRepository, times(1)).findAll(pageable);
    }
}
