package com.example.oauthprj.oauth2.service.impl;

import com.example.oauthprj.user.domain.User;
import com.example.oauthprj.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserOAuth2Service extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if ("kakao".equals(registrationId)) {
            return kakaoLoadUser(attributes, userRequest);
        } else {
            return kakaoLoadUser(attributes, userRequest);
        }
    }


    private OAuth2User kakaoLoadUser(Map<String, Object> attributes, OAuth2UserRequest userRequest) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");

        // member 가입 여부 확인
        User user = userRepository.findByEmail(email);
        if (user == null) {
            //create user
            user = User.createUser("kakao_" + UUID.randomUUID().toString().substring(0, 5), email, passwordEncoder.encode(UUID.randomUUID().toString()), nickname, "S");
            userRepository.save(user);
        } else {
            //login
        }
        Map<String, Object> loginAttribute = Map.of(
                "seq", user.getUserSeq(),
                "email", user.getEmail(),
                "id", user.getUserId(),
                "state", user.getUserState()
        );
        return new DefaultOAuth2User(
                Set.of(new SimpleGrantedAuthority("ROLE_USER")),
                loginAttribute,
                "id"
        );
    }
}
