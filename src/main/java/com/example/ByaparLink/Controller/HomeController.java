package com.example.ByaparLink.Controller;

import jakarta.servlet.http.HttpServletRequest;

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



}
