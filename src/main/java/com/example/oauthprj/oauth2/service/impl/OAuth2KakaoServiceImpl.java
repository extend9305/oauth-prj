package com.example.oauthprj.oauth2.service.impl;

import com.example.oauthprj.config.oauth2.properties.OAuth2ProviderProperties;
import com.example.oauthprj.oauth2.dto.OAuth2AuthInfoDto;
import com.example.oauthprj.oauth2.dto.OAuth2KakaoUserInfoDto;
import com.example.oauthprj.oauth2.service.Oauth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2KakaoServiceImpl implements Oauth2Service {
    private final OAuth2ProviderProperties oauth2ProviderProperties;

    @Override
    public OAuth2KakaoUserInfoDto kakaoLogin(OAuth2AuthInfoDto oauth2AuthInfoDto) {
        return null;
    }
}
