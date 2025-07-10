package com.example.oauthprj.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userSeq;
    @Column(nullable = false, unique = true)
    private String userId;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String userPwd;
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String userState;


    private User(String userId, String email, String userPwd, String userName, String userState) {
        this.userId = userId;
        this.email = email;
        this.userPwd = userPwd;
        this.userName = userName;
        this.userState = userState;
    }

    public static User createUser(String userId,String email, String userPwd, String userName, String userState) {
        return new User(userId, email, userPwd, userName, userState);
    }
}
