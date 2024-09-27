package com.md.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.md.backend.dto.Invoice.CreateInvoiceRequest;
import com.md.backend.dto.Item.ItemDto;
import com.md.backend.entity.Invoice;
import com.md.backend.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase()
public class InvoiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Test
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void createInvoice_ValidInput_SavesInvoice() throws Exception {
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

        // Actual request
        mockMvc.perform(post("/api/invoice/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvoiceRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Invoice created!"));

        List<Invoice> invoices = invoiceRepository.findAll();
        assertThat(invoices.size()).isEqualTo(1);
        assertThat(invoices.get(0).getCustomerName()).isEqualTo("customer");
        assertThat(invoices.get(0).getItems().size()).isEqualTo(2);
    }


}
