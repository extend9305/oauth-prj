package com.example.oauthprj.oauth2.service;

import com.example.oauthprj.oauth2.dto.OAuth2AuthInfoDto;
import com.example.oauthprj.oauth2.dto.OAuth2KakaoUserInfoDto;
import org.springframework.stereotype.Service;

@Service
public interface Oauth2Service {
    OAuth2KakaoUserInfoDto kakaoLogin(OAuth2AuthInfoDto oauth2AuthInfoDto);
}
