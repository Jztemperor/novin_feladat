package com.md.backend.service;

import com.md.backend.dto.ApplicationUser.ApplicationUserDto;
import com.md.backend.entity.ApplicationUser;
import com.md.backend.entity.Role;
import com.md.backend.projection.ApplicationUserProjection;
import com.md.backend.repository.ApplicationUserRepository;
import com.md.backend.repository.RoleRepository;
import com.md.backend.service.impl.ApplicationUserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationUserServiceTest {

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    @Mock
    private ApplicationUserProjection applicationUserProjection;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private ApplicationUserServiceImpl applicationUserService;

    @Test
    public void getAllUsersWithRoles_ReturnsPageDto() {
        // setup
        Pageable pageable = PageRequest.of(0, 10);
        Set<Role> authorities = Set.of(new Role(1L, "Felhasznalo", "description"));

        when(applicationUserProjection.getUserId()).thenReturn(1L);
        when(applicationUserProjection.getUsername()).thenReturn("username");
        when(applicationUserProjection.getAuthorities()).thenReturn(authorities);

        List<ApplicationUserProjection> userList = List.of(applicationUserProjection);
        Page<ApplicationUserProjection> userPage = new PageImpl<>(userList);

        when(applicationUserRepository.findAllProjectedBy(pageable)).thenReturn(userPage);

        // act
        Page<ApplicationUserDto> result = applicationUserService.getAllUsersWithRoles(pageable);

        // assert
        ApplicationUserDto applicationUserDto = result.getContent().get(0);
        assertEquals(1l, applicationUserDto.getId());
        assertEquals("username", applicationUserDto.getUsername());
        assertEquals(authorities, applicationUserDto.getAuthorities());

        // verify method call
        verify(applicationUserRepository).findAllProjectedBy(pageable);
    }

    @Test
    public void deleteUser_CorrectId_Success() {

        // arrange
        // user exists
        when(applicationUserRepository.existsById(1L)).thenReturn(true);

        // act
        applicationUserService.deleteUser(1L);

        // assert method call
        verify(applicationUserRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteUser_IncorrectId_ThrowsException() {
        // arrange
        // user does not exists
        when(applicationUserRepository.existsById(1L)).thenReturn(false);

        // act + assert
        assertThrows(EntityNotFoundException.class, () -> applicationUserService.deleteUser(1L));

        // assert that repo's delete method was never called
        verify(applicationUserRepository, times(0)).deleteById(1L);

    }

    @Test
    public void updateUserRole_CorrectInput_Success() {
        ApplicationUser user = new ApplicationUser();
        user.setUserId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setName("name");

        List<Long> roleIds = Arrays.asList(1L, 2L);
        Role role1 = new Role(1L, "role1", "description");
        Role role2 = new Role(2L, "role2", "description");
        Set<Role> roles = new HashSet<>(Arrays.asList(role1, role2));

        when(applicationUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findAllById(roleIds)).thenReturn(new ArrayList<>(roles));

        // Act
        applicationUserService.updateUserRole(1L, roleIds);

        // Assert
        assertEquals(new HashSet<>(roles), user.getAuthorities()); // Convert to Set for comparison
        verify(applicationUserRepository).save(user);
    }

    @Test
    public void updateUserRole_MissingRoles_ThrowsException() {
        ApplicationUser user = new ApplicationUser();
        user.setUserId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setName("name");

        // Arrange
        List<Long> roleIds = Arrays.asList(2L, 3L);
        Role role = new Role();
        role.setRoleId(2L);
        List<Role> roles = Arrays.asList(role); // only 1 role exists

        // User exists, only 1 role exists
        when(applicationUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findAllById(roleIds)).thenReturn(roles);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            applicationUserService.updateUserRole(1L, roleIds);
        });

        assertEquals("A következő szerepkörök nem találhatók: [3]", exception.getMessage());
        verify(applicationUserRepository, never()).save(any());

    }

    @Test
    public void updatedRoles_IncorrectUserId_ThrowsException() {
        List<Long> roleIds = Arrays.asList(2L, 3L);

        when(applicationUserRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            applicationUserService.updateUserRole(1L, roleIds);
        });

        assertEquals("A frissíteni kívánt felhasználó nem található!", exception.getMessage());
        verify(applicationUserRepository, never()).save(any());
    }

    @Test
    public void updatedRoles_EmptyRoleIds_ThrowsException() {
        ApplicationUser user = new ApplicationUser();
        user.setUserId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setName("name");
        List<Long> roleIds = new ArrayList<>();

        when(applicationUserRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            applicationUserService.updateUserRole(1L, roleIds);
        });

        assertEquals("A szerepek listája nem lehet üres!", exception.getMessage());
        verify(applicationUserRepository, never()).save(any());
    }
}
