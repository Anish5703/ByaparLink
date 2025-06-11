package com.example.ByaparLink.Repository;

import com.example.ByaparLink.Model.Token;
import com.example.ByaparLink.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepo extends JpaRepository<Token,Integer> {

    Token findByTokenName(String tokenName);
    Token findByUser(Users user);

}
