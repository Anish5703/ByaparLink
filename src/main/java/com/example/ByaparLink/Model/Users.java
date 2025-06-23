package com.example.ByaparLink.Model;

import com.example.ByaparLink.Model.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique=true)
    private String username;

    @Email(message="Valid email format required")
    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isActive;


    //Parameterized Constructor
    public Users( String username,String email,String password)
    {
        this.username = username;
        this.email = email;
        this.password = password;
        isActive = false;

    }
}
