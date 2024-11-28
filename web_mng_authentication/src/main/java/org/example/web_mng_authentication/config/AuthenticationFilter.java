package org.example.web_mng_authentication.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web_mng_authentication.jwt.TokenProvider;
import org.example.web_mng_authentication.user.dto.UserLoginRequestDto;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final TokenProvider tokenProvider;
    private final CutomAuthenticationProvider cutomAuthenticationProvider;
    final String TOKEN_ACCESS_KEY = "access-token";

    public AuthenticationFilter(CutomAuthenticationProvider cutomAuthenticationProvider, TokenProvider tokenProvider) {
        this.cutomAuthenticationProvider = cutomAuthenticationProvider;
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(cutomAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }

    //    Authentication 객체로부터 인증에 필요한 정보(username, password 등)를 받아오고, userDetailsService 인터페이스를
    //    구현한 객체(CustomUserDetailsService)로 부터 DB에 저장된 유저 정보를 받아온 후, password를 비교하고 인증이 완료되면 인증이 완료된 Authentication 객체를 리턴
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);
        // 사용자가 입력한 인증정보 받기, POST method 값이기 때문에 input stream으로 받았다.
        UserLoginRequestDto creds;
        try {
//            creds = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestDto.class);
            // 공백제거
            username = username.trim();
            log.info("authenticate run user name : {} ", username);
            // 호출 + 초기화

        } catch (RuntimeException e) {
            log.error(e.getLocalizedMessage());
        }

        UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(username, password);

        Authentication auth = cutomAuthenticationProvider.authenticate(upat);

        // 유저 로그인 정보를 받아오 인증정보를 리턴한다.
        return auth;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.info("doFilter run");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

//        UserLoginRequestDto userInput = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestDto.class);
//        System.out.println(userInput);

        if (!requiresAuthentication(httpRequest, httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication;

        try {
            authentication = attemptAuthentication(httpRequest, httpResponse);
            if(authentication == null){
                return;
            }
            cutomAuthenticationProvider.authenticate(authentication);


//            String token = httpResponse.getHeader("access-token");
//            System.out.println("token : " + token);
//
////            if (!hasLength(token) || "undefined".equals(token)) {
////                super.doFilter(request, httpResponse, chain);
//            if (!tokenProvider.validateToken(token)) {
//                // 토큰 유효성 검사는 API Gateway ReactiveAuthorization 클래스에서 미리 처리된다.
////                Claims claims = tokenProvider.getClaimsFromToken(token);
//                chain.doFilter(request, response);
//            } else  {
//                log.info("validate token");
//                Authentication authenticationResult = attemptAuthentication(httpRequest, httpResponse);
//                cutomAuthenticationProvider.authenticate(authenticationResult);
//                chain.doFilter(request, httpResponse);
//            }

//        } catch (ServletException | IOException e) {
        } catch (RuntimeException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            log.error("AuthenticationFilter doFilter error: {}", e.getMessage());
        }
        chain.doFilter(request, response);
    }
}
