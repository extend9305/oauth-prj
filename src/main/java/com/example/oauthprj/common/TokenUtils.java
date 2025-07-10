package com.example.oauthprj.common;

import com.example.oauthprj.common.dto.ValidTokenDto;
import com.example.oauthprj.config.security.dto.UserAuthDto;
import com.example.oauthprj.config.security.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TokenUtils {
    private static SecretKey JWT_SECRET_KEY;

    public TokenUtils(@Value("${jwt.secret}") String jwtSecretKey) {
        this.JWT_SECRET_KEY = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    private static Date createExipreDate(int calendarType, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendarType, amount);
        return calendar.getTime();
    }

    //jwt header 부분 생성
    private static Map<String, Object> createHeader() {
        return Jwts.header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .add("regDate", System.currentTimeMillis())
                .build();
    }

    //jwt claims 유저 정보로 생성
    private static Map<String, Object> createClaims(UserDto userDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDto.getUserId());
        claims.put("authorities", userDto.getAuthorities());
        return claims;
    }

    //jwt token 으로 부터 claims 추출
    private static Claims getTokenToClaims(String token) {
        log.info("getTokenToClaims::token:{}", token);
        return Jwts.parser()
                .verifyWith(JWT_SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public static ValidTokenDto isValidToken(String token) {
        try {
            Claims claims = getTokenToClaims(token);
            log.info("isValidToken::token:{}", token);
            log.info("isValidToken::userId:{}", claims.get("userId"));
            log.info("isValidToken::userNm:{}", claims.get("userNm"));
            return ValidTokenDto.builder()
                    .isValid(true)
                    .errorName(null)
                    .build();
        } catch (ExpiredJwtException exception) {
            log.error("Token is expired", exception);
            return ValidTokenDto.builder()
                    .isValid(false)
                    .errorName("TOKEN_EXPIRED")
                    .build();
        } catch (JwtException exception) {
            log.error("Token is not valid", exception);
            return ValidTokenDto.builder()
                    .isValid(false)
                    .errorName("TOKEN_INVALID")
                    .build();
        } catch (NullPointerException exception) {
            log.error("Token is null", exception);
            return ValidTokenDto.builder()
                    .isValid(false)
                    .errorName("TOKEN_NULL")
                    .build();
        }
    }
    // access token 생성
    public static String generateToken(UserDto userDto) {
        JwtBuilder builder = Jwts.builder()
                .setHeader(createHeader())                      //Header 구성
                .setClaims(createClaims(userDto))               //Payload - Calims 구성
                .subject(String.valueOf(userDto.getUserSeq()))   //PayLoad - Subject 구성
                .signWith(JWT_SECRET_KEY)
                .expiration(createExipreDate(Calendar.MINUTE,1));
        return builder.compact();
    }
    // refresh token 생성
    public static String generateRefreshToken(UserDto userDto) {
        JwtBuilder builder = Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaims(userDto))
                .subject(String.valueOf(userDto.getUserSeq()))
                .signWith(JWT_SECRET_KEY)
                .expiration(createExipreDate(Calendar.HOUR,1));
        return builder.compact();
    }


    public static UserDto getTokenToUserDto(String token) {
        Claims claims = getTokenToClaims(token);
        claims.get("userId").toString();
        return UserDto.builder()
                .userId(claims.get("userId").toString())
                .authorities(new ArrayList<>((List<UserAuthDto>)claims.get("authorities")))
                .build();
    }

    public static String getClaimsToUserId(String token) {
        Claims claims = getTokenToClaims(token);
        return String.valueOf(claims.get("userId"));
    }

    public static String getHeaderToToken(String header){
        return header.split(" ")[1];
    }

}
