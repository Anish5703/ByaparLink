package com.example.ByaparLink.Controller;

import com.example.ByaparLink.DTO.Login.LoginResponse;
import com.example.ByaparLink.DTO.Register.RegisterRequest;
import com.example.ByaparLink.DTO.Register.RegisterResponse;
import com.example.ByaparLink.Service.JwtService;
import com.example.ByaparLink.Service.OauthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/oauth")
public class OauthController {

    @Autowired
    private OauthService oauthService;

    //Endpoint to fetch email and also redirect to set username page
    @GetMapping("/register")
    public ResponseEntity<RegisterRequest> setUsername(@CookieValue(name="email",required = false)String email)
    {
        RegisterRequest registerRequest;
        if(email!=null) {
            registerRequest = new RegisterRequest(null, null, email);
            return ResponseEntity.status(HttpStatus.OK).body(registerRequest);
        }
        else {
            registerRequest = new RegisterRequest();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registerRequest);
        }
    }

    //Endpoint to register user for first time oauth login
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerOauth(@RequestBody RegisterRequest registerRequest,
                                                          HttpServletResponse servletResponse,HttpServletRequest servletRequest)
    {
      RegisterResponse registerResponse = oauthService.registerUser(registerRequest);
      if(!registerResponse.isError())
      {
         boolean isCookieSet =  oauthService.setJwtCookie(registerResponse.getUsername(),servletResponse,servletRequest);
               registerResponse.addMessage("isCookieSet",isCookieSet);
          return ResponseEntity.status(HttpStatus.OK).body(registerResponse);
      }
      else
      {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registerResponse);
      }
    }

    //home endpoint after auth login
    @GetMapping("/home")
    public ResponseEntity<LoginResponse> oauthHome(@CookieValue(name="jwt",required = false)String token)
    {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null,null,null,null,Map.of("status","No jwt token generated"),true));
        }

        LoginResponse loginResponse = oauthService.validateToken(token);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+token);
        if(!loginResponse.isError())
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(loginResponse);
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(loginResponse);

    }

    /*
   { Flow through oauth login }
    select account -> invoke successhandler ->
     *For previously Logged in user -> set jwt cookie -> redirected to (GET)"/api/oauth/home" -> receive json with jwt token and loginResponse;

     *For first time Logged in user -> redirected to (GET) "/api/oauth/register" -> receive json with email
                                    -> pass json with email and username -> Call (POST) "/api/oauth/register" -> set jwt cookie -> receive json with user info
                                    ->Call (GET) "/api/oath/home" -> receive json with jwt token and loginResponse



     */

}
