package com.md.backend.service;

import com.md.backend.dto.Authentication.LoginRequest;
import com.md.backend.dto.Authentication.LoginResponse;
import com.md.backend.dto.Authentication.RegistrationRequest;

public interface AuthenticationService {
    public void register(RegistrationRequest registrationRequest);
    public LoginResponse login(LoginRequest loginRequest);
}
