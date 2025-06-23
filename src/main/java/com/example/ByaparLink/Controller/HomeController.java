package com.example.ByaparLink.Controller;

import com.example.ByaparLink.Model.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HomeController {


    @GetMapping("/home")
    public String getHome(HttpServletRequest req)
    {

        StringBuilder url = new StringBuilder();
        url.append(req.getScheme())
                .append("://").append(req.getServerName()).append(":").append(req.getServerPort()).append(req.getContextPath());
        return "Click this link: <a href=\"" + url.toString() + "\">" + url.toString() + "</a>";
    }

    @GetMapping("/profile")
    public String getUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        String username = userPrincipal.getUsername();
        var role = userPrincipal.getAuthorities();
        return "Username : "+ username + " "+role;
    }



}
