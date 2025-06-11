package com.example.ByaparLink.Repository;

import com.example.ByaparLink.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<Users,Integer> {

    Users findByUsername(String username);
    Users findByEmail(String email);
}
