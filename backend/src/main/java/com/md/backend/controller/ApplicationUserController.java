package com.md.backend.controller;

import com.md.backend.dto.ApplicationUser.ApplicationUserDto;
import com.md.backend.service.ApplicationUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class ApplicationUserController {

    private final ApplicationUserService applicationUserService;

    public ApplicationUserController(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    @GetMapping
    public ResponseEntity<Page<ApplicationUserDto>> getUsernamesWithRoles(Pageable pageable) {
        return new ResponseEntity<Page<ApplicationUserDto>>(applicationUserService.getAllUsersWithRoles(pageable), HttpStatus.OK);
    }
}
