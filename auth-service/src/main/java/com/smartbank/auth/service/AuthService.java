package com.smartbank.auth.service;

import com.smartbank.auth.dto.LoginRequest;
import com.smartbank.auth.dto.LoginResponse;
import com.smartbank.auth.dto.UserRegistrationRequest;
import com.smartbank.auth.dto.UserRegistrationResponse;

public interface AuthService {

    UserRegistrationResponse register(
            UserRegistrationRequest request
    );

    LoginResponse login (
            LoginRequest loginRequest
    );
}