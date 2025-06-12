package com.example.ByaparLink.Controller;

import com.example.ByaparLink.DTO.Login.LoginRequest;
import com.example.ByaparLink.DTO.Login.LoginResponse;
import com.example.ByaparLink.DTO.Register.RegisterRequest;
import com.example.ByaparLink.DTO.Register.RegisterResponse;
import com.example.ByaparLink.Service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    //Endpoint to register new user
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest req, HttpServletRequest servletRequest) {


        RegisterResponse resp = authService.registerUser(req,servletRequest);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        if (!resp.isError())
            return ResponseEntity.status(HttpStatus.CREATED).headers(header).body(resp);
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(header).body(resp);
        }

      //Endpoint to confirm registration
    @GetMapping("/confirmRegistration")
    public ResponseEntity<RegisterResponse> confirmRegistration(@RequestParam(name="token")String token)
    {
        RegisterResponse resp = authService.validateRegisterConfirmation(token);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        if(!resp.isError())
            return ResponseEntity.status(HttpStatus.CREATED).headers(header).body(resp);
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(header).body(resp);
    }
    //Endpoint to send confirmation token
    @PutMapping("/resendConfirmation")
    public ResponseEntity<RegisterResponse> resendConfirmation(@RequestParam(name="email")String email,HttpServletRequest servletRequest)
    {
        RegisterResponse resp = authService.resendConfirmationToken(email,servletRequest);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        if(!resp.isError())
            return ResponseEntity.status(HttpStatus.OK).headers(header).body(resp);
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(header).body(resp);

    }

    //Endpoint to login user
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest)
    {
        LoginResponse resp = authService.loginUser(loginRequest);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        if(!resp.isError()) {
            header.set("Authorization","Bearer "+resp.getToken());
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(header).body(resp);
        }
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(header).body(resp);
    }

    }




