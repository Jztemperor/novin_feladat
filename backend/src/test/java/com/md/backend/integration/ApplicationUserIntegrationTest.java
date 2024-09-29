package com.md.backend.integration;

import com.md.backend.entity.ApplicationUser;
import com.md.backend.repository.ApplicationUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class ApplicationUserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Test
    @Transactional
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void deleteUser_Success() throws Exception {
        ApplicationUser user = new ApplicationUser();
        user.setUsername("user");
        user.setName("user");
        user.setPassword("password");
        applicationUserRepository.save(user);

        mockMvc.perform(delete("/api/user/{id}", user.getUserId()))
                .andExpect(status().is(204))
                .andExpect(content().string("Felhasználó törölve!"));

        assertFalse(applicationUserRepository.existsById(1L));
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_Adminisztrator"})
    public void deleteUser_NotFound() throws Exception {
        mockMvc.perform(delete("/api/user/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errors.message").value("A törölni kívánt felhasználó nem található!"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
