package com.taskflow.services;

import com.taskflow.dtos.requests.users.LoginRequest;
import com.taskflow.dtos.requests.users.RegisterRequest;
import com.taskflow.dtos.responses.users.LoginResponse;
import com.taskflow.dtos.responses.users.ProfileResponse;
import com.taskflow.exceptions.InvalidCredentialsException;
import com.taskflow.exceptions.UserAlreadyExistsException;
import com.taskflow.exceptions.UserNotFoundException;
import com.taskflow.jwts.JwtService;
import com.taskflow.models.UserModel;
import com.taskflow.repositories.IUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImp implements IAuthService{

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImp(IUserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void register(RegisterRequest userRequest) {
        Optional<UserModel> userByEmail = userRepository.findByEmail(userRequest.getEmail());
        if(userByEmail.isPresent()){
            throw new UserAlreadyExistsException("Ya existe un usuario con email: " + userRequest.getEmail() + " registrado. ");
        }
        Optional<UserModel> userByMobilePhone = userRepository.findByMobilePhone(userRequest.getMobilePhone());
        if(userByMobilePhone.isPresent()){
            throw new UserAlreadyExistsException("Ya existe un usuario con telefono: " + userRequest.getMobilePhone() + " registrado.");
        }

        UserModel userFinal = UserModel.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .mobilePhone(userRequest.getMobilePhone())
                .build();

        userRepository.save(userFinal);
    }

    @Override
    public LoginResponse login(LoginRequest userRequest) {
        Optional<UserModel> userByEmail = userRepository.findByEmail(userRequest.getEmail());
        if(!userByEmail.isPresent()){
            throw new UserNotFoundException(userRequest.getEmail());
        }
        UserModel userFinal = userByEmail.get();

        if(!passwordEncoder.matches(userRequest.getPassword(), userFinal.getPassword())){
            throw new InvalidCredentialsException("Contraseña incorrecta. ");
        }


        String userToken = jwtService.getToken(userFinal.getId(), userFinal.getEmail());

        LoginResponse loginResponse = LoginResponse.builder()
                .token(userToken)
                .build();
        return loginResponse;
    }


}
