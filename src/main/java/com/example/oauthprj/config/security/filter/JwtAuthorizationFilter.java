package com.example.oauthprj.config.security.filter;

import com.example.oauthprj.common.TokenUtils;
import com.example.oauthprj.common.dto.ValidTokenDto;
import com.example.oauthprj.config.security.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final String ACCESS_TOKEN_HEADER_KEY = "Authorization";
    private static final String REFRESH_TOKEN_HEADER_KEY = "x-refresh-token";

    private static final String HTTP_METHOD_OPTIONS = "OPTIONS";
    private static final List<String> WHITELIST_URLS = Arrays.asList(
            "/api/v1/user/login",
            "/api/v1/token/token",
            "/user/login",
            "/api/v1/oauth2/naver",
            "/api/v1/oauth2/kakao",
            "/token/token"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessTokenHeader = request.getHeader(ACCESS_TOKEN_HEADER_KEY);

            // [STEP1] 토큰이 필요하지 않는 API 호출 발생 혹은 토큰이 필요없는 HTTP Method OPTIONS 호출 시 : 아래 로직 처리 없이 다음 필터로 이동
            if (WHITELIST_URLS.contains(request.getRequestURI()) || HTTP_METHOD_OPTIONS.equalsIgnoreCase(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;     // 종료
            }


            // [access token] header 체크
            if (!StringUtils.isEmpty(accessTokenHeader)) {
                String accessToken = TokenUtils.getHeaderToToken(accessTokenHeader);
                // [access token] 검증
                ValidTokenDto validAccessToken = TokenUtils.isValidToken(accessToken);
                // [access token] 성공시 다음 filter chain 이동
                if (validAccessToken.isValid()) {
                    filterChain.doFilter(request, response);
                } else {
                    // [access token] 실패 case 별 처리
                    // [access token] access token 기간 만료시 refresh token으로 재 발행 시도
                    if ("TOKEN_EXPIRED".equals(validAccessToken.getErrorName())) {
                        String refreshTokenHeader = request.getHeader(REFRESH_TOKEN_HEADER_KEY);
                        String refreshToken = TokenUtils.getHeaderToToken(refreshTokenHeader);

                        ValidTokenDto validRefreshToken = TokenUtils.isValidToken(refreshToken);
                        if (validRefreshToken.isValid()) {
                            UserDto tokenToUserDto = TokenUtils.getTokenToUserDto(refreshToken);
                            String generatedAccessToken = TokenUtils.generateToken(tokenToUserDto);
                            sendToClientAccessToken(generatedAccessToken, response);
                            filterChain.doFilter(request, response);
                        }
                    } else {
                        throw new RuntimeException("재 로그인 필요");
                    }

                }

            }


        } catch (Exception e) {

        }
    }

    private void sendToClientAccessToken(String token, HttpServletResponse response) {
        Map<String, Object> resultMap = new HashMap<>();
        ObjectMapper om = new ObjectMapper();
        resultMap.put("status", 401);
        resultMap.put("failMsg", null);
        resultMap.put("accessToken", token);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write(om.writeValueAsString(resultMap));
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            log.error("[-] 결과값 생성에 실패하였습니다 : {}", e);
        }

    }

}

