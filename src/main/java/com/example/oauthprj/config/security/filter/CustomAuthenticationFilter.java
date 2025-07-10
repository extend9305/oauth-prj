package com.example.oauthprj.config.security.filter;

import com.example.oauthprj.config.security.dto.UserDetailsDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    /**
     * form 로그인 시도시 파라미터 정보를 바탕으로 authentication 객체 생성
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authRequest;
        try {
            authRequest = getAuthentication(request);
            setDetails(request, authRequest);
        }catch (Exception e) {
            throw new RuntimeException("Authentication failed", e);
        }
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * request 의 id, password 로 토큰을 발급한다.
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
            UserDetailsDto user = mapper.readValue(request.getInputStream(), UserDetailsDto.class);

            log.debug(String.format("CustomAuthenticationFilter :: userId : %s, userPw : %s",user.getUserId(),user.getUserPwd()));

            return new UsernamePasswordAuthenticationToken(user.getUserId(),user.getUserPwd());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
