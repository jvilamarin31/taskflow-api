package com.taskflow.controllers;


import com.taskflow.dtos.requests.users.LoginRequest;
import com.taskflow.dtos.requests.users.RegisterRequest;
import com.taskflow.dtos.responses.users.LoginResponse;
import com.taskflow.services.IAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest userRequest){
        authService.register(userRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest userRequest){
        return ResponseEntity.ok(authService.login(userRequest));
    }

}
