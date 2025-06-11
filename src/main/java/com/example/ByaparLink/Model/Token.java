package com.example.ByaparLink.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@Entity
public class Token {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int Id;

    @Column(nullable = false)
    private String tokenName;

    @ManyToOne
    @JoinColumn(name="users_id",nullable=false)
    private Users user;


    public Token(String tokenName, Users user) {
        this.tokenName = tokenName;
        this.user = user;
    }
}
