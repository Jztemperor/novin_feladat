package com.md.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.md.backend.dto.Invoice.CreateInvoiceRequest;
import com.md.backend.dto.Invoice.InvoiceDto;
import com.md.backend.dto.Item.ItemDto;
import com.md.backend.service.InvoiceService;
import com.md.backend.service.impl.InvoiceServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private InvoiceService invoiceService;

    @Test
    @WithMockUser(authorities = {"SCOPE_Adminisztrator", "SCOPE_Konyvelo"})
    public void createInvoice_CorrectInput_ShouldReturnOkResponse() throws Exception {
        // setup
        CreateInvoiceRequest createInvoiceRequest = new CreateInvoiceRequest();
        createInvoiceRequest.setCustomerName("customer");
        createInvoiceRequest.setIssueDate(LocalDate.now());
        createInvoiceRequest.setDueDate(createInvoiceRequest.getIssueDate().plusDays(1));

        ItemDto itemDto = new ItemDto();
        itemDto.setItemName("x");
        itemDto.setPrice(5000);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setItemName("y");
        itemDto2.setPrice(5000);

        createInvoiceRequest.setItems(Arrays.asList(itemDto, itemDto2));
        createInvoiceRequest.setComment("comment");

        lenient().doNothing().when(invoiceService).createInvoice(Mockito.any(CreateInvoiceRequest.class));

        // Mock request
        mockMvc.perform(post("/api/invoice/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createInvoiceRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Invoice created!"));
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_Felhasznalo", "SCOPE_Adminisztrator", "SCOPE_Konyvelo"})
    public void getInvoices_ShouldReturnOkResponse() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        InvoiceDto invoiceDto1 = new InvoiceDto();
        invoiceDto1.setId(1L);
        invoiceDto1.setCustomerName("Customer A");
        invoiceDto1.setIssueDate(LocalDate.now());
        invoiceDto1.setDueDate(LocalDate.now().plusDays(30));
        invoiceDto1.setComment("First Invoice");
        invoiceDto1.setTotalPrice(100.0);

        InvoiceDto invoiceDto2 = new InvoiceDto();
        invoiceDto2.setId(2L);
        invoiceDto2.setCustomerName("Customer B");
        invoiceDto2.setIssueDate(LocalDate.now());
        invoiceDto2.setDueDate(LocalDate.now().plusDays(30));
        invoiceDto2.setComment("Second Invoice");
        invoiceDto2.setTotalPrice(200.0);

        List<InvoiceDto> invoiceDtos = Arrays.asList(invoiceDto1, invoiceDto2);
        Page<InvoiceDto> invoicePage = new PageImpl<>(invoiceDtos, pageable, invoiceDtos.size());

        // Mock the service to return the mocked Page
        lenient().when(invoiceService.getAllInvoices(any(Pageable.class))).thenReturn(invoicePage);

        mockMvc.perform(get("/api/invoice")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_Felhasznalo", "SCOPE_Adminisztrator", "SCOPE_Konyvelo"})
    public void getInvoice_IncorrectId_ReturnsExceptionResponse() throws Exception {
        long invalidInvoiceId = 999L;
        lenient().when(invoiceService.getInvoice(invalidInvoiceId)).thenThrow(new EntityNotFoundException("A keresett számla nem található!"));

        // Act & Assert
        mockMvc.perform(get("/api/invoice/{id}", invalidInvoiceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errors.message").value("A keresett számla nem található!"))
                .andDo(print());
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_Felhasznalo", "SCOPE_Adminisztrator", "SCOPE_Konyvelo"})
    public void getInvoice_CorrectId_ReturnsOkResponse() throws Exception {
        long invoiceId = 1L;
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setId(invoiceId);
        invoiceDto.setCustomerName("customer");
        invoiceDto.setTotalPrice(100.0);

        lenient().when(invoiceService.getInvoice(invoiceId)).thenReturn(invoiceDto);

        // Act & Assert
        mockMvc.perform(get("/api/invoice/{id}", invoiceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOK());
    }
}
