package com.md.backend.service;

import com.md.backend.dto.ApplicationUser.ApplicationUserDto;
import com.md.backend.entity.Role;
import com.md.backend.projection.ApplicationUserProjection;
import com.md.backend.repository.ApplicationUserRepository;
import com.md.backend.service.impl.ApplicationUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApplicationUserServiceTest {

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    @Mock
    private ApplicationUserProjection applicationUserProjection;

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

}
