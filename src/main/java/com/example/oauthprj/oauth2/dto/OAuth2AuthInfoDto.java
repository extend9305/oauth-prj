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
public class OAuth2AuthInfoDto {
    private String code;              // 토큰 받기 요청에 필요한 인가 코드
    private String error;             // 인증 실패 시 반환되는 에러 코드
    private String errorDescription;  // 인증 실패 시 반환되는 에러 메시지
    private String state;             // 요청 시 전달한 state 값과 동일한 값
}
