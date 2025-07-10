package com.example.oauthprj.config.security.handler;

import com.example.oauthprj.common.TokenUtils;
import com.example.oauthprj.config.security.dto.UserAuthDto;
import com.example.oauthprj.config.security.dto.UserDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // login 성공한 사용자
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        List<UserAuthDto> authList = oAuth2User.getAuthorities().stream()
                .map(authority -> {
                    return new UserAuthDto(authority.getAuthority());
                }).collect(Collectors.toList());

        Map<String, Object> attributes = oAuth2User.getAttributes();

        UserDto userDto = UserDto.builder()
                .userSeq((int) attributes.get("seq"))
                .userId(attributes.get("id").toString())
                .userState(attributes.get("state").toString())
                .authorities(authList)
                .build();

        // 응답 데이터 구성
        Map<String,Object> responseMap = new HashMap<>();

        //인증 성공시 token 생성
        if("D".equals(userDto.getUserState())) {
            responseMap.put("resultCode",9001);
            responseMap.put("token",null);
            responseMap.put("failMsg", "휴먼 계정입니다.");
        }else{
            String accessToken = TokenUtils.generateToken(userDto);
            String refreshToken = TokenUtils.generateRefreshToken(userDto);
            responseMap.put("resultCode",200);
            responseMap.put("userInfo",userDto);
            responseMap.put("accessToken", accessToken);
            responseMap.put("refreshToken", refreshToken);
            responseMap.put("failMsg", null);
            response.addHeader("Authorization", "BEARER " + accessToken);
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 리디렉트할 프론트엔드 URI
        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oAuth2LoginSuccess")
                .queryParam("accessToken", responseMap.get("accessToken"))
                .queryParam("refreshToken", responseMap.get("refreshToken"))
                .build().toUriString();

        response.sendRedirect(redirectUrl);

        super.onAuthenticationSuccess(request, response, authentication);

    }
}
