package org.example.web_mng_authentication.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web_mng_authentication.domain.UserInfo;
import org.example.web_mng_authentication.domain.UserRepository;
import org.example.web_mng_authentication.user.dto.UserInfoDto;
import org.example.web_mng_authentication.user.dto.UserResponseAllDto;
import org.example.web_mng_authentication.user.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserApiService implements UserDetailsService {

    final UserRepository userRepository;
//    final PasswordEncoder passwordEncoder;
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Long signIn(UserInfoDto userInfoDto) throws Exception {
        return userRepository.save(userInfoDto.toEntity(passwordEncoder)).getUserNo();
    }

    public Page<UserResponseDto> findById (String userId, Pageable pageable) throws Exception {
        Page<UserInfo> entity = userRepository.findByUserName(userId, pageable);
        Page<UserResponseDto> dto = new UserResponseDto().toPage(entity);
        return dto;
    }

    public UserResponseAllDto findByUserId(String userId) throws Exception {
        UserInfo entity = userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("01"));

        return new UserResponseAllDto(entity);
    }

    public void updateRefreshToken(String userId, String updateRefreshToken) {
        UserInfo user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("01"));

        user.updateRefreshToken(updateRefreshToken);

        log.info("refresh token : ",updateRefreshToken);
    }

    // 로그인 후처리 (로그인 성공, 실패 처리)
    public void loginCallback(String userId, Boolean successAt, String failContent) {
        Optional<UserInfo> result = userRepository.findByUserId(userId);

        // Optional 객체가 값을 가지고 있다면 true, 값이 없다면 false 리턴
        if(result.isPresent()) {

            UserInfo user = result.get();
            if (Boolean.TRUE.equals(successAt)) {
                user.successLogin();
            } else {
                user.failLogin();
                log.error(failContent);
            }
        }
    }


    // user 정보를 entity로 받아 user로 반환해준다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userRepository.findByUserId(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        log.info("Success find user {}", username);
        log.info(userInfo.toString());
        return User.builder()
                .username(userInfo.getUserId())
                .password(userInfo.getUserPassword())
                .build();
    }
}
