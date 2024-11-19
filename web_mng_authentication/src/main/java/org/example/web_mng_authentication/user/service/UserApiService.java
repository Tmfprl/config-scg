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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserApiService {

    final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long singIn(UserInfoDto userInfoDto) throws Exception {
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


}
