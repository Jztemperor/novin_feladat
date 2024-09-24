package com.md.backend.service;

import com.md.backend.dto.Authentication.RegistrationRequest;

public interface AuthenticationService {
    public void register(RegistrationRequest registrationRequest);
}
