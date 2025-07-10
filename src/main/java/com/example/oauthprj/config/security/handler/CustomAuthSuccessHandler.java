package com.example.oauthprj.config.security.handler;

import com.example.oauthprj.common.TokenUtils;
import com.example.oauthprj.config.security.dto.UserAuthDto;
import com.example.oauthprj.config.security.dto.UserDetailsDto;
import com.example.oauthprj.config.security.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CustomAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final ObjectMapper objectMapper;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("onAuthenticationSuccess");
        // 사용자 인증 객체 반환
        UserDetailsDto userDetailsDto = (UserDetailsDto) authentication.getPrincipal();

        List<UserAuthDto> authList = userDetailsDto.getAuthorities().stream()
                .map(authority -> {
                    return new UserAuthDto(authority.getAuthority());
                }).collect(Collectors.toList());

        UserDto userDto = UserDto.builder()
                .userSeq(userDetailsDto.getUserSeq())
                .userId(userDetailsDto.getUserId())
                .userState(userDetailsDto.getUserState())
                .authorities(authList)
                .build();

        // 응답 데이터 구성
        Map<String,Object> responseMap = new HashMap<>();

        //인증 성공시 token 생성
        if("D".equals(userDetailsDto.getUserState())) {
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


        objectMapper.writeValue(response.getWriter(), responseMap);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
