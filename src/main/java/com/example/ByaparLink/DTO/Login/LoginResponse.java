package com.example.ByaparLink.DTO.Login;

import com.example.ByaparLink.Model.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {


    private String username;
    private String email;
    private Role role;
    private String token;
    private Map<String,Object> message;
    private boolean error;
}
