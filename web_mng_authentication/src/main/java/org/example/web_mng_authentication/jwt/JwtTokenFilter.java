package org.example.web_mng_authentication.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
//    public static final String AUTHORIZATION_HEADER = "Authorization";
////    Bearer : JWT 혹은 OAuth에 대한 토큰을 사용한다. (RFC 6750)
//    public static final String BEARER_PREFIX = "Bearer ";
//
//    private final TokenProvider tokenProvider;
//
//
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        // 1. Request Header 에서 토큰을 꺼냄
//        String jwt = resolveToken(request);
//
//        // 2. validateToken 으로 토큰 유효성 검사
//        // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 SecurityContext 에 저장
//        if (StringUtils.hasText(jwt) && tokenProvider.validateToken토큰유효성검사_이건게이트웨이에서?(jwt)) {
//            Authentication authentication = tokenProvider.getToken메소드만들어서가져오기(jwt);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//
//        // 토큰 유효성 검사를 해보려고 했는데 잘안되서 회사 프로젝트를 확인해보니 게이트웨이 측에서 유효성을 검사함
//        // 전체적으로 모르는 패키지이고 어렵다 , 처음보는 타입(Mono?)
//
//        filterChain.doFilter(request, response);
//    }
//
//    private String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
//            return bearerToken.split(" ")[1].trim();
//        }
//        return null;
//
    }
}

/**
 *
 * 여기에 있어야 하는거 :
 *  access token provider
 *  refresh token provider
 *  add token in header
 *  response to gateway
 *
 * 저기에 있어야 하는거 :
 *  validation token
 *  ....
 *
 */