package org.example.web_mng_authentication.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web_mng_authentication.exception.ServiceCoustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    final String AUTHORIZATION_HEADER = "access-token";
//    Bearer : JWT 혹은 OAuth에 대한 토큰을 사용한다. (RFC 6750)
//    public static final String BEARER_PREFIX = "Bearer ";
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
         // 1. Request Header 에서 토큰을 꺼냄
        String jwt = response.getHeader(AUTHORIZATION_HEADER);
        log.info("response.getHeader(AUTHORIZATION_HEADER) : {}", jwt);

        try {
            if (jwt != null && tokenProvider.validateToken(jwt)) {
                Authentication auth = tokenProvider.getAuthentication(jwt);
                // 정상 토큰이면 토큰을 통해 생성한 Authentication 객체를 SecurityContext에 저장
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (ServiceCoustomException e) {
            SecurityContextHolder.clearContext();
            response.sendError(404, e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);


//        // 2. validateToken 으로 토큰 유효성 검사
//        // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 SecurityContext 에 저장
//        if (StringUtils.hasText(jwt) && tokenProvider.validateToken토큰유효성검사_이건게이트웨이에서?(jwt)) {
//            Authentication authentication = tokenProvider.getToken메소드만들어서가져오기(jwt);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
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