package com.md.backend.repository;

import com.md.backend.entity.ApplicationUser;
import com.md.backend.projection.ApplicationUserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByUsername(String username);
    boolean existsByUsername(String username);
    Page<ApplicationUserProjection> findAllProjectedBy(Pageable pageable);
}
