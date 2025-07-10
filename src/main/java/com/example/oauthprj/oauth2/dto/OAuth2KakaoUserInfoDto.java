package com.example.oauthprj.oauth2.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OAuth2KakaoUserInfoDto {
    private String id;
    private int statusCode;                 // 상태 코드
    private String email;                   // 이메일
    private String nickname;                // 닉네임
    private String profileImageUrl;         // 프로필 이미지 URL
    private String thumbnailImageUrl;       // 썸네일 이미지 URL
    private String name;                    // [Biz] 사용자 이름
    private String ageRange;                // [Biz] 사용자 나이 범위
    private String birthday;                // [Biz] 사용자 생일
    private String gender;                  // [Biz] 사용자 성별
}
