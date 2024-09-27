package com.md.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.md.backend.dto.Invoice.CreateInvoiceRequest;
import com.md.backend.dto.Item.ItemDto;
import com.md.backend.service.InvoiceService;
import com.md.backend.service.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
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
}
