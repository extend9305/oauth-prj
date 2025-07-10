package com.example.oauthprj.config.security;

import com.example.oauthprj.config.security.filter.CustomAuthenticationFilter;
import com.example.oauthprj.config.security.filter.JwtAuthorizationFilter;
import com.example.oauthprj.config.security.handler.CustomAuthFailureHandler;
import com.example.oauthprj.config.security.handler.CustomAuthSuccessHandler;
import com.example.oauthprj.config.security.handler.OAuth2AuthenticationSuccessHandler;
import com.example.oauthprj.config.security.provider.CustomAuthenticationProvider;
import com.example.oauthprj.oauth2.service.impl.UserOAuth2Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final UserOAuth2Service userOAuth2Service;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)                                                                                                  // CSRF 보호 비활성화
                .cors(cors->corsConfigurationSource())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(jwtAuthorizationFilter(), BasicAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   // 세션 미사용 (JWT 사용)
                .addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                //oauth2 login
                .oauth2Login(config ->
                        config.redirectionEndpoint(redirect->redirect.baseUri("/api/v1/oauth2/*"))
                                .successHandler(oAuth2AuthenticationSuccessHandler)
                                .userInfoEndpoint(userInfoEndpointConfig ->
                                        userInfoEndpointConfig.userService(userOAuth2Service)))
                .formLogin(AbstractHttpConfigurer::disable)                                                     // 폼 로그인 비활성화
                .build();
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
        customAuthenticationFilter.setFilterProcessesUrl("/api/v1/user/login");
        customAuthenticationFilter.setAuthenticationSuccessHandler(customAuthSuccessHandler());
        customAuthenticationFilter.setAuthenticationFailureHandler(customAuthFailureHandler());
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter();
    }
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(customAuthenticationProvider());
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService,passwordEncoder);
    }

    @Bean
    public CustomAuthSuccessHandler customAuthSuccessHandler() {
        return new CustomAuthSuccessHandler(objectMapper);
    }

    @Bean
    public CustomAuthFailureHandler customAuthFailureHandler() {
        return new CustomAuthFailureHandler();
    }


    /**
     * 10. CORS에 대한 설정을 커스텀으로 구성합니다.
     *
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));      // 허용할 오리진
        configuration.setAllowedMethods(List.of("*"));                          // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(List.of("*"));                          // 모든 헤더 허용
        configuration.setAllowCredentials(true);                                    // 인증 정보 허용
        configuration.setMaxAge(3600L);                                             // 프리플라이트 요청 결과를 3600초 동안 캐시
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);             // 모든 경로에 대해 이 설정 적용
        return source;
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler();
    }
}
