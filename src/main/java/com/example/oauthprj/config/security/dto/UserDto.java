package com.example.oauthprj.config.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {
    // 사용자 시퀀스
    private int userSeq;
    // 사용자 아이디
    private String userId;
    // 사용자 상태
    private String userState = "S";

    private List<UserAuthDto> authorities;


    @Builder(toBuilder = true)
    private UserDto(int userSeq, String userId, String userState, List<UserAuthDto> authorities) {
        this.userSeq = userSeq;
        this.userId = userId;
        this.userState= userState;
        this.authorities = authorities;
    }
}
