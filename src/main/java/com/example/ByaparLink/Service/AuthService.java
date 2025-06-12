package com.example.ByaparLink.Service;

import com.example.ByaparLink.DTO.Login.LoginRequest;
import com.example.ByaparLink.DTO.Login.LoginResponse;
import com.example.ByaparLink.DTO.Register.RegisterRequest;
import com.example.ByaparLink.DTO.Register.RegisterResponse;
import com.example.ByaparLink.Mapper.UserMapper;
import com.example.ByaparLink.Model.Enum.Role;
import com.example.ByaparLink.Model.Token;
import com.example.ByaparLink.Model.Users;
import com.example.ByaparLink.Repository.TokenRepo;
import com.example.ByaparLink.Repository.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TokenRepo tokenRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authManager;


    //Method to login user with credentials
    public LoginResponse loginUser(LoginRequest req)
    {
        try{
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(),req.getPassword()));
            Users user = userRepo.findByUsername(req.getUsername());
            Map<String,Object> map = new HashMap<>();
            map.put("status","Login Successfully");
            String token = jwtService.generateToken(user.getUsername());
            return UserMapper.toLoginResponse(user,token,map,false);
        }
  catch(AuthenticationException ex)
        {
            Users user = userRepo.findByUsername(req.getUsername());
            Map<String,Object> map = new HashMap<>();
            if(user==null)
                map.put("username","Username invalid");

            else if(!user.getPassword().equals(encoder.encode(req.getPassword())))
                map.put("password","Password invalid");

            map.put("status","Login Failed");
            return UserMapper.toLoginResponse(req,map,true);

        }
    }




    //Method to register new user in database with encoding raw password
    public RegisterResponse registerUser(RegisterRequest req,HttpServletRequest servletRequest) {

        //Validating request for email and username
        RegisterResponse response = validateRegisterRequest(req);
        if (response.isError())
            return response;

        //Processing Valid request
        Users user = UserMapper.toEntity(req);
        user.setRole(Role.USER);
        user.setPassword(encoder.encode(user.getPassword()));
        Map<String, Object> map = new HashMap<>();
        try{

            Users newUser = userRepo.save(user);

            //prepare response to send
            map.put("status","Check mail for confirmation link");
            response.setRole(newUser.getRole());
            response.setError(false);
            //send confirmation token to the user email address
            sendConfirmationToken(newUser,servletRequest);

        }
        catch(Exception e)
        {
            map.put("status", "Registration Failed");
            map.put("exception",e.getLocalizedMessage());
            response.setError(true);
        }

            response.setMessage(map);
            return response;

    }

    //Method that validates the request and provides the response
    public RegisterResponse validateRegisterRequest(RegisterRequest req) {
        RegisterResponse resp = new RegisterResponse();
        resp.setUsername(req.getUsername());
        resp.setEmail(req.getEmail());

        Map<String, Object> map = new HashMap<String, Object>();

        if (isUsernameExists(req.getUsername())) {
            map.put("username", "Username Already Exists");
            resp.setError(true);
        }
        if (isEmailExists(req.getEmail())) {
            map.put("email", "Email Already Exists");
            resp.setError(true);

        }
        resp.setMessage(map);
        return resp;
    }


    //Method to validate token and Set Users Active flag true
    public RegisterResponse validateRegisterConfirmation(String tokenName)
    {
        Token token = tokenRepo.findByTokenName(tokenName);
        Map<String,Object> map = new HashMap<>();

        if(token!=null)
        {
            Users user = token.getUser();
            user.setActive(true);
            userRepo.save(user);
            map.put("status","Registration Successful");
            return UserMapper.toRegisterResponse(token.getUser(),map,false);

        }
        else
        {
            map.put("status","Registration Successful");
            RegisterResponse resp = new RegisterResponse();
            resp.setUsername(null);
            resp.setEmail(null);
            resp.setRole(null);
            resp.setError(true);
            resp.setMessage(map);
            return resp;
        }
    }

    //Method to send registration confirmation token
    public void sendConfirmationToken(Users user,HttpServletRequest servletRequest) throws MessagingException
    {
        //generating token and storing it to the repo with username
        String token = generateToken();
        tokenRepo.save(new Token(token,user));

        //Concating url and token
        String confirmationLink = getConfirmationUrl(servletRequest)+token;

        //sending confirmation mail to the user
        String htmlContent = emailService.buildConfirmationEmail(user.getUsername(),confirmationLink);
        emailService.sendHtmlEmail(user.getEmail(),"Confirmation Mail",htmlContent);
    }

    //Method to resend registration confirmation token
    public RegisterResponse resendConfirmationToken(String email,HttpServletRequest servletRequest)
    {
        Map<String,Object> map = new HashMap<>();

        if(email.isEmpty())
        {
            map.put("status","Null Email field");
            return new RegisterResponse(null,null,null,map,true);
        }
        else {
               Users user = userRepo.findByEmail(email);
               if(user==null)
               {
                   map.put("email","No registration found with this email id");
                   map.put("status","Fill the registration form first");
                   return new RegisterResponse(null,email,null,map,true);
               }
               else if(user.isActive())
               {
                   map.put("status","Account already active");
                   return new RegisterResponse(user.getUsername(), user.getEmail(), user.getRole(),map,true);
               }
               else
               {
                   try {
                       //deleting old token from the database
                       tokenRepo.delete(tokenRepo.findByUser(user));
                       //resending confirmation token
                       sendConfirmationToken(user, servletRequest);
                       map.put("status", "Check mail for confirmation link");
                       return new RegisterResponse(user.getUsername(), user.getEmail(), user.getRole(), map, false);
                   }
                   catch(MessagingException e)
                   {
                       map.put("status","Failed to send confirmation link");
                       return new RegisterResponse(user.getUsername(), user.getEmail(), user.getRole(),map,true);
                   }
               }
        }

    }


    //Method to check if username exists in database
    public boolean isUsernameExists(String username) {
        return userRepo.findByUsername(username) != null;

    }

    //Method to check if email exists in database
    public boolean isEmailExists(String email) {
        return userRepo.findByEmail(email) != null;
    }




    //Method to generate confirmation URL excluding token
    public String getConfirmationUrl(HttpServletRequest request)
    {
       return  "http://"+request.getServerName()+":"+request.getServerPort()+"/api/auth/confirmRegistration?token=";
    }

    //Method to generate random token
    public String generateToken()
    {
        String token = UUID.randomUUID().toString();
        while(tokenRepo.findByTokenName(token)!=null)
        {
            token = UUID.randomUUID().toString();
        }
        return token;
    }

}