package com.taskflow.services;

import com.taskflow.dtos.responses.users.ProfileResponse;
import com.taskflow.exceptions.UserNotFoundException;
import com.taskflow.models.UserModel;
import com.taskflow.repositories.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImp implements IUserService{

    private final IUserRepository userRepository;

    public UserServiceImp(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ProfileResponse getProfile(String userId) {
        Optional<UserModel> userById = userRepository.findById(userId);
        if(!userById.isPresent()){
            throw new UserNotFoundException(userId);
        }
        UserModel userFinal = userById.get();
        ProfileResponse profileResponse = ProfileResponse.builder()
                .name(userFinal.getName())
                .email(userFinal.getEmail())
                .mobilePhone(userFinal.getMobilePhone())
                .build();
        return profileResponse;
    }
}
