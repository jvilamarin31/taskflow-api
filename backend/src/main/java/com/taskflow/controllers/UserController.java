package com.taskflow.controllers;

import com.taskflow.dtos.responses.users.ProfileResponse;
import com.taskflow.models.UserModel;
import com.taskflow.services.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal UserModel user){
        return ResponseEntity.ok(userService.getProfile(user.getId()));
    }
}
