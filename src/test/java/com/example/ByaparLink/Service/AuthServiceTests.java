package com.example.ByaparLink.Service;

import com.example.ByaparLink.DTO.Login.LoginRequest;
import com.example.ByaparLink.DTO.Login.LoginResponse;
import com.example.ByaparLink.DTO.Register.RegisterRequest;
import com.example.ByaparLink.DTO.Register.RegisterResponse;
import com.example.ByaparLink.Model.Enum.Role;
import com.example.ByaparLink.Model.Token;
import com.example.ByaparLink.Model.Users;
import com.example.ByaparLink.Repository.TokenRepo;
import com.example.ByaparLink.Repository.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension .class)
public class AuthServiceTests {

    @Mock
    UserRepo userRepo;
    @Mock
    TokenRepo tokenRepo;
    @Mock
    EmailService emailService;
    @Mock
    JwtService jwtService;
    @Mock
    AuthenticationManager authManager;
    @Mock
    BCryptPasswordEncoder encoder;
    @Mock
    HttpServletRequest servletRequest;
    @Mock
    JavaMailSender mailSender;

    @InjectMocks
    AuthService authService;

    private RegisterRequest buildRegisterRequest()
    {
        return new RegisterRequest("garund","rawPassword","garund@gmail.com");
    }
    private Users buildExistingUser()
    {
        Users user = new Users();
        user.setUsername("garund");
        user.setEmail("garund@gmail.com");
        user.setActive(true);
        user.setRole(Role.USER);
        user.setPassword("encodedPassword");
        return user;
    }

    private Token buildExistingToken()
    {
        Token token = new Token();
        token.setId(1);
        token.setTokenName("confirmation@Link");
        token.setUser(buildExistingUser());
        return token;
    }

    @Test
    @Tag("Success Response")
    public void registerUser_ConfirmationLink_ReturnsSuccessResponse() throws MessagingException {
        RegisterRequest registerReq = buildRegisterRequest();

        //Mocked data in db
        Users user = buildExistingUser();


        //Mock user db and encoder
        Mockito.when(userRepo.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(encoder.encode(registerReq.getPassword())).thenReturn("encodedPassword");
        Mockito.when(userRepo.findByUsername(registerReq.getUsername())).thenReturn(null);
        Mockito.when(userRepo.findByEmail(registerReq.getEmail())).thenReturn(null);

        //Mock token db and email service
        Mockito.when(tokenRepo.save(any(Token.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(tokenRepo.findByTokenName(anyString())).thenReturn(null);
        Mockito.when(servletRequest.getServerName()).thenReturn("localhost");
        Mockito.when(servletRequest.getServerPort()).thenReturn(8081);

      RegisterResponse registerResp =  authService.registerUser(registerReq,servletRequest);

        //check if the user credentials is saved properly with encoded password
        assertEquals(registerReq.getUsername(),registerResp.getUsername(),"Expected requested username and response username same");
        assertEquals("encodedPassword",user.getPassword());
      //check if register response is not null
      assertNotNull(registerResp,"Register Response is null");
      //check if error flag in response is false
      assertFalse(registerResp.isError(),"Register Response has error");
      //check if user role is valid
      assertEquals(Role.USER,registerResp.getRole(),"Expected users role USER");
      //check if the response message is cosistent
      assertEquals("Check mail for confirmation link",registerResp.getMessage().get("status"),"Expected Confirmation message");


    }

    @Test
    @Tag("Error Response")
    public void registerUser_UsernameAlreadyExists_ReturnsErrorResponse()
    {
        RegisterRequest registerReq = buildRegisterRequest();

        //Mocked data in db
        Users existingUser = buildExistingUser();
        //Mock users db
        Mockito.when(userRepo.findByUsername(anyString())).thenReturn(existingUser);

        RegisterResponse registerResp = authService.registerUser(registerReq,servletRequest);

        assertNotNull(registerResp,"Expected Register response not null");
        assertEquals("Username Already Exists",registerResp.getMessage().get("username"),"Expected error Username Already Exists");
        assertTrue(registerResp.isError(),"Expected error flag true");


    }

    @Test
    @Tag("Error Response")
    public void registerUser_EmailAlreadyExists_ReturnsErrorResponse()
    {
        RegisterRequest registerReq = buildRegisterRequest();

        //Mocked data in db
        Users existingUser = buildExistingUser();

        //Mock users db
        Mockito.when(userRepo.findByEmail(anyString())).thenReturn(existingUser);

        RegisterResponse registerResp = authService.registerUser(registerReq,servletRequest);

        assertNotNull(registerResp,"Expected Register Response not null");
        assertTrue(registerResp.isError(),"Expected error flag true");
        assertEquals("Email Already Exists",registerResp.getMessage().get("email"),"Expected error Email Already Exists");
    }

    @Test
    @Tag("Success Response")
    public void validateRegisterConfirmation_SuccessfulRegistration_ReturnsSuccessResponse()
    {
        String tokenName= "confirmation@Link";

        //Mocked data in db
        Token token = buildExistingToken();

        //Mock token db
        Mockito.when(tokenRepo.findByTokenName(anyString())).thenReturn(token);

        RegisterResponse registerResp = authService.validateRegisterConfirmation(tokenName);

        assertNotNull(registerResp,"Expected Register Response not null");
        assertFalse(registerResp.isError(),"Expected error flag false");
        assertEquals("Registration Successful",registerResp.getMessage().get("status"));

    }
    @Test
    @Tag("Error Response")
    public void validateRegisterConfirmation_FailedRegistration_ReturnsErrorResponse()
    {
        String tokenName = "confirmation@Link";

        //Mock token db
        Mockito.when(tokenRepo.findByTokenName(anyString())).thenReturn(null);

        RegisterResponse registerResp = authService.validateRegisterConfirmation(tokenName);


        assertNotNull(registerResp,"Expected Register Response not null");
        assertTrue(registerResp.isError(),"Expected error flag true");
        assertEquals("Registration Unsuccessful",registerResp.getMessage().get("status"));
    }

}
