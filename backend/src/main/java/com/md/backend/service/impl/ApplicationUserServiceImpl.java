package com.md.backend.service.impl;

import com.md.backend.dto.ApplicationUser.ApplicationUserDto;
import com.md.backend.entity.ApplicationUser;
import com.md.backend.entity.Role;
import com.md.backend.projection.ApplicationUserProjection;
import com.md.backend.repository.ApplicationUserRepository;
import com.md.backend.repository.RoleRepository;
import com.md.backend.service.ApplicationUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;
    private final RoleRepository roleRepository;

    public ApplicationUserServiceImpl(ApplicationUserRepository applicationUserRepository, RoleRepository roleRepository) {
        this.applicationUserRepository = applicationUserRepository;
        this.roleRepository = roleRepository;
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

    @Override
    public void updateUserRole(Long userId, List<Long> roleIds) {
        // Get user - check if it exists
        ApplicationUser user = applicationUserRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("A frissíteni kívánt felhasználó nem található!"));

        // Get roles - check if they exists
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));

        if(roles.isEmpty()) {
            throw new EntityNotFoundException("A szerepek listája nem lehet üres!");
        }

        if(roles.size() != roleIds.size()) {
            Set<Long> existingRoleIds = roles.stream().map(Role::getRoleId).collect(Collectors.toSet());
            List<Long> missingRoleIds = roleIds.stream()
                    .filter(roleId -> !existingRoleIds.contains(roleId))
                    .toList();

            throw new EntityNotFoundException("A következő szerepkörök nem találhatók: " + missingRoleIds);
        }

        // Update user with new roles
        user.setAuthorities(roles);
        applicationUserRepository.save(user);
    }
}
