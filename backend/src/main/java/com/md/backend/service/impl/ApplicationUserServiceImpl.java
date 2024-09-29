package com.md.backend.service.impl;

import com.md.backend.dto.ApplicationUser.ApplicationUserDto;
import com.md.backend.projection.ApplicationUserProjection;
import com.md.backend.repository.ApplicationUserRepository;
import com.md.backend.service.ApplicationUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;

    public ApplicationUserServiceImpl(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    /**
     * Retrieves a paginated list of all users (username, id) along with their roles.
     *
     * @param pageable the pagination and sorting information
     * @return a paginated list of ApplicationUserDto containing user details and their roles
     */
    @Override
    public Page<ApplicationUserDto> getAllUsersWithRoles(Pageable pageable) {
        Page<ApplicationUserProjection> users = applicationUserRepository.findAllProjectedBy(pageable);

        return users.map(user -> new ApplicationUserDto(
               user.getUserId(),
               user.getUsername(),
               user.getAuthorities()
               ));
    }

    @Override
    public void deleteUser(Long id) {
        boolean exists = applicationUserRepository.existsById(id);

        if(!exists) {
            throw new EntityNotFoundException("A törölni kívánt felhasználó nem található!");
        }

        applicationUserRepository.deleteById(id);
    }
}
