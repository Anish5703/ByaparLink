package com.example.ByaparLink.Mapper;


import com.example.ByaparLink.DTO.Login.LoginRequest;
import com.example.ByaparLink.DTO.Login.LoginResponse;
import com.example.ByaparLink.DTO.Register.RegisterRequest;
import com.example.ByaparLink.DTO.Register.RegisterResponse;
import com.example.ByaparLink.Model.Users;

import java.util.Map;

//Mapper between Entity and DTOs


public class UserMapper {

    /**
     * Converts {@link RegisterRequest} to {@link Users}
     * @param req the registration request DTO containing user input
     * @return a {@link Users} entity
     */
    public static Users toEntity(RegisterRequest req)
    {
        return new Users(
                req.getUsername(),
                req.getEmail(),
                req.getPassword()
        );
    }

   //User Entity to LoginResponse DTO
    public static LoginResponse toLoginResponse(Users user, String token, Map<String,Object> message, boolean error)
    {
        return new LoginResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                token,
                message,
                error
        );
    }

    //LoginRequest to loginResponse DTO
    public static LoginResponse toLoginResponse(LoginRequest request,Map<String,Object> message,boolean error)
    {
        return new LoginResponse(
                request.getUsername(),
                null,
                null,
                null,
                message,
                error
        );
    }

    //User Entity to RegisterResponse DTO
    public static RegisterResponse toRegisterResponse(Users user,Map<String,Object> message,boolean error)
    {
         return new RegisterResponse(
                 user.getUsername(),
                 user.getEmail(),
                 user.getRole(),
                 message,
                 error
         );
    }
}
