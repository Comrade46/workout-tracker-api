package com.workouttracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// "Forgot password" form on the login page
public class PasswordHelpRequestDTO {

    @NotBlank(message = "Username or email is required")
    @Size(max = 100, message = "Username or email is too long")
    private String usernameOrEmail;

    @Size(max = 300, message = "Message must not exceed 300 characters")
    private String message;

    public String getUsernameOrEmail() { return usernameOrEmail; }
    public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
