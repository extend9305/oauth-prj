package com.example.oauthprj.user.service;

import com.example.oauthprj.config.security.dto.UserDetailsDto;
import com.example.oauthprj.user.domain.User;
import com.example.oauthprj.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // 사용자 id 를 조회하여 사용자 검증
        if (userId == null || userId.isEmpty()) {
            throw new AuthenticationServiceException("사용자 ID가 없습니다.");
        }
        User user = userRepository.findByUserId(userId);

        if(user == null) {
            throw new UsernameNotFoundException(userId + " not found");
        }
        return new UserDetailsDto(
                user.getUserSeq()
                , user.getUserId()
                , user.getUserPwd()
                , user.getUserName()
                , user.getUserState()
                , List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
