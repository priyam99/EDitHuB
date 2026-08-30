package com.edithub.user.controller;

import com.edithub.common.ApiResponse;
import com.edithub.user.dto.SkillDto;
import com.edithub.user.dto.UpdateProfileRequest;
import com.edithub.user.dto.UserDto;
import com.edithub.user.model.User;
import com.edithub.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/me")
    public ApiResponse<UserDto> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        UserDto userDto = userService.getUserById(currentUser.getId());
        return ApiResponse.ok(userDto);
    }

    @PatchMapping("/users/me")
    public ApiResponse<UserDto> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UpdateProfileRequest request) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        UserDto updatedUser = userService.updateProfile(currentUser.getId(), request);
        return ApiResponse.ok(updatedUser, "Profile updated successfully");
    }

    @GetMapping("/users/{username}")
    public ApiResponse<UserDto> getPublicProfile(@PathVariable String username) {
        UserDto userDto = userService.getProfileByUsername(username);
        return ApiResponse.ok(userDto);
    }

    @GetMapping("/skills")
    public ApiResponse<List<SkillDto>> getSkills() {
        List<SkillDto> skills = userService.getAllSkills();
        return ApiResponse.ok(skills);
    }
}
