package com.example.ByaparLink.DTO.Register;

import com.example.ByaparLink.Model.Enum.Role;
import lombok.*;


import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterResponse {

    private String username;
    private String email;
    private Role role;
    private Map<String,Object> message;
    private boolean error;


    public void addMessage(String key,Object value)
    {
        message.put(key,value);
    }
}
