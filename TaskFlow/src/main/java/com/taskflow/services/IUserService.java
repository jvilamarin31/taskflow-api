package com.taskflow.services;

import com.taskflow.dtos.responses.users.ProfileResponse;

public interface IUserService {
    public ProfileResponse getProfile(String userId);
}
