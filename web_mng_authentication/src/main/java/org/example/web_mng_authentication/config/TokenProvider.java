package org.example.web_mng_authentication.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web_mng_authentication.domain.UserInfo;
import org.example.web_mng_authentication.domain.UserRepository;
import org.example.web_mng_authentication.user.dto.UserResponseAllDto;
import org.example.web_mng_authentication.user.service.UserApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final UserApiService userService;
    private final UserRepository userRepository;

    @Value("${token.secret}")
    private String TOKEN_SECRET;

    @Value("${token.expiration_time}")
    private String TOKEN_EXPIRATION_TIME;

    @Value("${token.refresh_time}")
    private String TOKEN_REFRESH_TIME;

    final String TOKEN_ACCESS_KEY = "access-token";
    final String TOKEN_CLAIM_USER_NAME = "userName";
    final String TOKEN_CLAIM_EMAIL = "email";

    public void createTokenAndAddHeader(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain chain, Authentication authResult) {
        // 로그인 성공 후 토큰 처리
        String userId = authResult.getName();
        String userName = "";
        String email = "";
        try {
            UserResponseAllDto user = userService.findByUserId(userId);
            userName = user.getUserName();
            email = user.getEmail();
        }catch (Exception e) {
            log.error("Exception : {}", e);
        }


        // JWT Access 토큰 생성
        String accessToken = createAccessToken(userId, userName, email);
        String refreshToken = createRefreshToken();
        userService.updateRefreshToken(userId, refreshToken);
        response.addHeader(TOKEN_ACCESS_KEY, accessToken);
    }


    public String createAccessToken(String userId, String userName, String email) {
        log.info("user id : {}", userId);
        log.info("userName : {}", userName);
        log.info("email : {}", email);
        return Jwts.builder()
                .setSubject(userId)
                .claim(TOKEN_CLAIM_USER_NAME, userName)
                .claim(TOKEN_CLAIM_EMAIL, email)
                // 토큰 유효기간
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(TOKEN_EXPIRATION_TIME)))
                // sing key를 지정(jwt 생성 라이브러리인 jjwt가 지정된 key에 허용된 가장 안전한 알고리즘은 결정하게 해준다.)
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
                // 토큰 유효기간
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(TOKEN_EXPIRATION_TIME)))
                // sing key를 지정(jwt 생성 라이브러리인 jjwt가 지정된 key에 허용된 가장 안전한 알고리즘은 결정하게 해준다.)
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
                .compact();
    }

    /**
     * AuthenticationFilter.doFilter 메소드에서 UsernamePasswordAuthenticationToken 정보를 세팅할 때 호출된다.
     *
     * @param token
     * @return
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * JWT Refresh Token 생성
     * 중복 로그인을 허용하려면 user domain 에 있는 refresh token 값을 반환하고 없는 경우에만 생성하도록 처리한다.
     *
     * @return refresh token
     */
    private String createRefreshToken() {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(TOKEN_REFRESH_TIME)))
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
                .compact();
    }

    public String updateRefreshToken(String userId, String updateRefreshToken) {
        UserInfo user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("01"));

        user.updateRefreshToken(updateRefreshToken);

        return user.getRefreshToken();
    }

}
