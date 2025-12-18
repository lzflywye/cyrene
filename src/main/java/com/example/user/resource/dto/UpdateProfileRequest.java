package com.example.user.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "Name is required") @Size(max = 50, message = "Please enter a name of 50 characters or less") String displayName) {

}
