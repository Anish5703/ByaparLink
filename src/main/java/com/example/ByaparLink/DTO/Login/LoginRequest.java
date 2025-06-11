package com.example.ByaparLink.DTO.Login;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {

    @NotBlank(message="Username required")
    private String username;

    @NotBlank(message="Password required")
    private String password;
}
