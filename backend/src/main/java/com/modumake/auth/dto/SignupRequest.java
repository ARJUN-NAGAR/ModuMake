package com.modumake.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class SignupRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    private String phoneNumber;

    @NotBlank
    @Size(min = 6)
    private String password;
}
