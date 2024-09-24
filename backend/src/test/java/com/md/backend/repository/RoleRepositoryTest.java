package com.md.backend.repository;

import com.md.backend.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    public void setUp() {
        Role role = new Role();
        role.setAuthority("test_role");
        testEntityManager.persist(role);
    }

    @Test
    public void findByAuthority_RoleFound_ReturnsRoleOptional() {
        Optional<Role> roleOptional = roleRepository.findByAuthority("test_role");

        assertThat(roleOptional)
                .isPresent()
                .hasValueSatisfying(role ->
                        assertThat(role.getAuthority()).isEqualTo("test_role"));

    }

    @Test
    public void findByAuthority_RoleNotFound_ReturnsEmptyOptional() {
        Optional<Role> roleOptional = roleRepository.findByAuthority("x");
        assertThat(roleOptional).isEmpty();
    }
}
