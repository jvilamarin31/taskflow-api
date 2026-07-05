package com.taskflow.services;

import com.taskflow.dtos.requests.users.LoginRequest;
import com.taskflow.dtos.requests.users.RegisterRequest;
import com.taskflow.dtos.responses.users.LoginResponse;
import com.taskflow.exceptions.InvalidCredentialsException;
import com.taskflow.exceptions.UserAlreadyExistsException;
import com.taskflow.exceptions.UserNotFoundException;
import com.taskflow.jwts.JwtService;
import com.taskflow.models.UserModel;
import com.taskflow.repositories.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImpTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImp authService;

    private final String email = "test@gmail.com";
    private final String mobilePhone = "3000000000";

    private UserModel createUser() {
        return UserModel.builder()
                .id("user123")
                .email(email)
                .mobilePhone(mobilePhone)
                .build();
    }

    @Test
    void registerUser_shouldSaveUser() {
        //Given
        RegisterRequest request = new RegisterRequest();
        request.setName("Julian Eduardo Pino");
        request.setEmail("julianPino@gmail.com");
        request.setPassword("12345");
        request.setMobilePhone("3005004521");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByMobilePhone(request.getMobilePhone())).thenReturn(Optional.empty());

        //When
        authService.register(request);

        //Then
        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    void registerUser_whenEmailExist_shouldThrowException() {
        //Given
        RegisterRequest request = new RegisterRequest();
        request.setName("Julian Eduardo Pino");
        request.setEmail(email);
        request.setPassword("12345");
        request.setMobilePhone("3005004521");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(createUser()));

        //When y Then
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void registerUser_whenMobilePhoneExist_shouldThrowException() {
        //Given
        RegisterRequest request = new RegisterRequest();
        request.setName("Julian Eduardo Pino");
        request.setEmail("julianPino@gmail.com");
        request.setPassword("12345");
        request.setMobilePhone(mobilePhone);

        when(userRepository.findByMobilePhone(request.getMobilePhone())).thenReturn(Optional.of(createUser()));

        //When y Then
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void loginUser_whenValidCredentials_shoulReturnToken() {
        //Given
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword("12345");

        UserModel userExist = createUser();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userExist));
        when(passwordEncoder.matches(request.getPassword(), userExist.getPassword())).thenReturn(true);
        when(jwtService.getToken(userExist.getId() , userExist.getEmail())).thenReturn("mocked-jwt-token");

        //When
        LoginResponse response = authService.login(request);

        //Then
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        verify(jwtService, times(1)).getToken(userExist.getId(), userExist.getEmail());
    }

    @Test
    void loginUser_whenUserNotFound_shouldThrowException() {
        //Given
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword("12345");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        //When y Then
        assertThrows(UserNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void loginUser_whenPasswordNotMatch_shouldThrowException() {
        //Given
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword("12345");

        UserModel userExist = createUser();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userExist));
        when(passwordEncoder.matches(request.getPassword(), userExist.getPassword())).thenReturn(false);

        //When y Then
        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

}
