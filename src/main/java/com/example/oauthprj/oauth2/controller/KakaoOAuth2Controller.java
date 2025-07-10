//package com.example.oauthprj.oauth2.controller;
//
//import com.example.oauthprj.oauth2.dto.OAuth2AuthInfoDto;
//import com.example.oauthprj.oauth2.dto.OAuth2KakaoUserInfoDto;
//import com.example.oauthprj.oauth2.service.Oauth2Service;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/v1/oauth2/kakao")
//@RequiredArgsConstructor
//public class KakaoOAuth2Controller {
//    private final Oauth2Service oauth2Service;
//
//    @GetMapping("")
//    public ResponseEntity<String> kakaoLogin(
//            @RequestParam(required = false) String code,
//            @RequestParam(required = false) String error,
//            @RequestParam(required = false) String error_description,
//            @RequestParam(required = false) String state
//    ) {
//        OAuth2KakaoUserInfoDto oAuth2KakaoUserInfoDto = oauth2Service.kakaoLogin(new OAuth2AuthInfoDto(code, error, error_description, state));
//        log.debug(code);
//        return new ResponseEntity<>(oAuth2KakaoUserInfoDto.toString(), HttpStatus.OK);
//    }
//}
