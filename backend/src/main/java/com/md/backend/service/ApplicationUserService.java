package com.md.backend.service;

import com.md.backend.dto.ApplicationUser.ApplicationUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationUserService {
    Page<ApplicationUserDto> getAllUsersWithRoles(Pageable pageable);
    void deleteUser(Long id);
}
