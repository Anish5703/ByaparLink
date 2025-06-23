package com.example.ByaparLink.Service;

import com.example.ByaparLink.DTO.Login.LoginResponse;
import com.example.ByaparLink.DTO.Register.RegisterRequest;
import com.example.ByaparLink.DTO.Register.RegisterResponse;
import com.example.ByaparLink.Mapper.UserMapper;
import com.example.ByaparLink.Model.Enum.Role;
import com.example.ByaparLink.Model.Users;
import com.example.ByaparLink.Repository.UserRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OauthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtService jwtService;




//Method to register user for first attempt of oauth login
    public RegisterResponse registerUser(RegisterRequest registerRequest)
    {
        boolean usernameExists = isUsernameExists(registerRequest.getUsername());

        if(usernameExists)
        {
            Map<String , Object > map = new HashMap<>(Map.of("username", "Username already exists", "status", "Registration Unsuccessfull"));
            return new RegisterResponse(registerRequest.getUsername(), registerRequest.getEmail(), null, map, true);
        }
        else
        {
            try {
                Users user = UserMapper.toEntity(registerRequest);
                user.setRole(Role.USER);
                user.setActive(true);
                Users newUser = userRepo.save(user);
                Map<String , Object > map = new HashMap<>(Map.of("status","Registration Successful"));
                return UserMapper.toRegisterResponse(newUser, map, false);
            }
            catch(Exception e)
            {
                System.out.println("Unable to save new user "+registerRequest.getUsername());
                Map<String , Object > map = new HashMap<>(Map.of("status","Registration Unsuccessfull","exception",e.getLocalizedMessage()));
                return new RegisterResponse(registerRequest.getUsername(), registerRequest.getEmail(), null, map, true);

            }
        }
    }

    //Method to set email in cookie
    public boolean setEmailCookie(String email,HttpServletResponse servletResponse,HttpServletRequest servletRequest)
    {
        if(email == null)
            return false;
        else
        {
            boolean isProduction = !servletRequest.getServerName().equals("localhost");

            Cookie cookie = new Cookie("email", email);
            cookie.setHttpOnly(true);
            cookie.setSecure(isProduction);
            cookie.setPath("/");
            cookie.setMaxAge(1200);
            servletResponse.addCookie(cookie);
            return true;
        }
    }

    //Method to generate and set jwt in cookie
    public boolean setJwtCookie(String username, HttpServletResponse servletResponse,HttpServletRequest servletRequest)
    {
        if(username == null)
            return false;
        else
        {
            boolean isProduction = !servletRequest.getServerName().equals("localhost");

            String token = jwtService.generateToken(username);
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(isProduction);
            cookie.setPath("/");
            cookie.setMaxAge(1200);
            servletResponse.addCookie(cookie);
            return true;

        }
    }

    //Method to validate and create  login response using jwt token
    public LoginResponse validateToken(String token)
    {
        String username =(String) jwtService.extractUsername(token);
        Users user = userRepo.findByUsername(username);
        if(user != null)
            return UserMapper.toLoginResponse(user,token,Map.of("status" , "Login Successfully"),false);
        else
            return UserMapper.toLoginResponse(user,token,Map.of("status","Token invalid"),true);
    }

    //Method to check if username exists in database
    public boolean isUsernameExists(String username) {
        return userRepo.findByUsername(username) != null;

    }

    //Method to check if email exists in database
    public boolean isEmailExists(String email) {
        return userRepo.findByEmail(email) != null;
    }
}
