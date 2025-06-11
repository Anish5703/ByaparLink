package com.example.ByaparLink.Service;

import com.example.ByaparLink.Model.UserPrincipal;
import com.example.ByaparLink.Model.Users;
import com.example.ByaparLink.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username)
    {
        Users user = userRepo.findByUsername(username);
        if(user == null)
        {
            throw new UsernameNotFoundException("NO user found with username : "+username);
        }
        return new UserPrincipal(user);

    }
}
