package org.example.web_mng_authentication.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.example.web_mng_authentication.user.service.UserApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class TokenProvider {

    private final UserApiService userService;

    public TokenProvider(UserApiService userService) {
        this.userService = userService;
    }

    @Value("${token.secret}")
    private String TOKEN_SECRET;

    @Value("${token.expiration_time}")
    private String TOKEN_EXPIRATION_TIME;

    @Value("${token.refresh_time}")
    private String TOKEN_REFRESH_TIME;

    final String TOKEN_ACCESS_KEY = "access-token";


    private String createAccessToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
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
}
