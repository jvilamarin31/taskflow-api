package com.taskflow.services;

import com.taskflow.dtos.requests.users.LoginRequest;
import com.taskflow.dtos.requests.users.RegisterRequest;
import com.taskflow.dtos.responses.users.LoginResponse;
import com.taskflow.dtos.responses.users.ProfileResponse;

public interface IAuthService {
    public void register(RegisterRequest userRequest);
    public LoginResponse login(LoginRequest userRequest);
}
