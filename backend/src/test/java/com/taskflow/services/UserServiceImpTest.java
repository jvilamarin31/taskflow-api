package com.taskflow.services;

import com.taskflow.dtos.responses.users.ProfileResponse;
import com.taskflow.exceptions.UserNotFoundException;
import com.taskflow.models.UserModel;
import com.taskflow.repositories.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImpTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserServiceImp userService;

    private String userId = "user123";

    private UserModel createUser() {
        return UserModel.builder()
                .id(userId)
                .name("name")
                .email("email@gmail.com")
                .mobilePhone("3005004521")
                .build();
    }

    @Test
    void getProfile_whenUserExist_shouldReturnProfile() {
        //Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(createUser()));

        //When
        ProfileResponse response = userService.getProfile(userId);

        //Then
        assertNotNull(response);
        assertEquals("name", response.getName());
        assertEquals("email@gmail.com", response.getEmail());
        assertEquals("3005004521", response.getMobilePhone());

    }

    @Test
    void getProfile_whenUserNotFound_shouldThrowException() {
        //Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //When y Then
        assertThrows(UserNotFoundException.class, () -> userService.getProfile(userId));
    }
}
