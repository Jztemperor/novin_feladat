package com.md.backend.repository;

import com.md.backend.entity.ApplicationUser;
import com.md.backend.entity.Role;
import com.md.backend.projection.ApplicationUserProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase()
public class ApplicationUserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @BeforeEach
    public void setUp() {
        Role role = new Role();
        role.setAuthority("test_role");
        testEntityManager.persist(role);

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        ApplicationUser user = new ApplicationUser();
        user.setUsername("test");
        user.setPassword("test");
        user.setName("test");
        user.setAuthorities(roles);
        testEntityManager.persist(user);
    }

    @Test
    public void findByUsername_UserFound_ReturnsUserOptional() {
        Optional<ApplicationUser> userOptional = applicationUserRepository.findByUsername("test");

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getUsername()).isEqualTo("test"));
    }

    @Test
    public void findByUsername_UserNotFound_ReturnsEmptyOptional() {
        Optional<ApplicationUser> userOptional = applicationUserRepository.findByUsername("x");
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void existsByUsername_UserFound_ReturnsTrue() {
        boolean doesExists = applicationUserRepository.existsByUsername("test");
        assertThat(doesExists).isTrue();
    }

    @Test
    public void existsByUsername_UserNotFound_ReturnsFalse() {
        boolean doesExists = applicationUserRepository.existsByUsername("x");
        assertThat(doesExists).isFalse();
    }

    @Test
    public void findAllProjectedBy_ReturnsProjectionContent() {
        Pageable pageable = PageRequest.of(0,10);
        Page<ApplicationUserProjection> projections = applicationUserRepository.findAllProjectedBy(pageable);

        List<ApplicationUserProjection> projectionList = projections.getContent();
        assertThat(projectionList).hasSize(1);
        assertThat(projectionList.get(0).getUsername()).isEqualTo("test");
        assertThat(projectionList.get(0).getAuthorities()).hasSize(1);
    }
}
