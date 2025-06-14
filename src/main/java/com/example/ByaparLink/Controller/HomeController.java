package com.example.ByaparLink.Controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HomeController {


    @GetMapping("/home")
    public String getHome(HttpServletRequest req)
    {
        String username = "anish";
        String msg = "http://"+req.getServerName()+":"+req.getServerPort()+req.getContextPath();
        return "Click this link: <a href=\"" + msg + "\">" + msg + "</a>";
    }



}
