package org.example.web_mng_authentication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.web_mng_authentication.user.dto.UserLoginRequestDto;
import org.example.web_mng_authentication.user.service.UserApiService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;

import static org.springframework.util.StringUtils.hasLength;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final TokenProvider tokenProvider;
    private final UserApiService userService;
    private final CutomAuthenticationProvider cutomAuthenticationProvider;
    BCryptPasswordEncoder passwordEncoder;

    // error! : Password Not found
    // pw를 맞게 입력하여도 pw를 찾을 수 없다는 에러가 발생한다. bcrypt 암호화로 바꾼 뒤로 match가 되지 않는다.
    // 바꾼이유는 인증시에 passwoedencodre 방식을 사용해서는 안된다고 하길래 사용한건데 로그인이 되질 않는다.
    // 하지만 예전에 passwordencode 방식을 사용하여 저장된 사용자 정보를 통해서는 로그인이 잘된다.
    // 저장된 암호문을 bcrypt 형식으로 인식하지 못하는건...이건 상관 없을 듯하다. 그냥 입력받은 평문을 암호화시켜서 db에 저장된 암호문과 비교하는건데
    // 단방향 암호라 복호화 할 일도 없는데 왜지 왜왜왜지 왤까 이유가 뭘까
    // 암호화된 형식을 보면 차이가 없다. 뭐지
    // id3 , pass -> 된다__암호화된 pw에 약간의 차이가 있지만 이게 문제일까?
    // userId3, pass -> 안된다
    public AuthenticationFilter(AuthenticationManager authenticationManager, TokenProvider tokenProvider, UserApiService userService,
                                BCryptPasswordEncoder passwordEncoder, CutomAuthenticationProvider cutomAuthenticationProvider) {
        this.passwordEncoder = passwordEncoder;
        this.cutomAuthenticationProvider = cutomAuthenticationProvider;
        super.setAuthenticationManager(authenticationManager);
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

//    @Bean
//    public AuthenticationProvider authenticationProvider() {
////        return new AuthenticationProvider(, passwordEncoder);
//        return new UsernamePasswordAuthenticationToken()
//    }

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

        // 사용자가 입력한 인증정보 받기, POST method 값이기 때문에 input stream으로 받았다.
        UserLoginRequestDto creds;
        try {
            creds = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestDto.class);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        // 호출 + 초기화
        UsernamePasswordAuthenticationToken upat = null;

        // 로그인 dto로 변환 + 리스트로 변환(담다)
        upat = new UsernamePasswordAuthenticationToken(
                creds.getUserId(),
                creds.getPassword(),
                new ArrayList<>());

        // 유저 로그인 정보를 받아오 인증정보를 리턴한다.
        return getAuthenticationManager().authenticate(upat);
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        log.info("=============================== successful authentication =================================");// 토큰 생성 및 response header add
        tokenProvider.createTokenAndAddHeader(request, response, chain, authResult);
        // 로그인 성공 후처리
        userService.loginCallback(authResult.getName(), true, "");
    }

    @Override
    public void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        String failContent = failed.getMessage();
        if (failed instanceof InternalAuthenticationServiceException) {
            log.info("{} 해당 사용자가 없습니다", request.getAttribute("userId"));
        } else if (failed instanceof BadCredentialsException) {
            failContent = "패스워드 인증에 실패하였습니다. " + failContent;
        }
        response.setStatus(HttpStatus.OK.value());

        String error = failed.getMessage();

        if(error.equals("Bad credentials")) {
            error = "-1";
        }

        response.setHeader("error", error);

        // 로그인 실패 후처리
        String userId = (String) request.getAttribute("userId");
        userService.loginCallback(userId, false, failContent);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            String token = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);

            if (!hasLength(token) || "undefined".equals(token)) {
                super.doFilter(request, httpResponse, chain);
            } else {
                // 토큰 유효성 검사는 API Gateway ReactiveAuthorization 클래스에서 미리 처리된다.
                Claims claims = tokenProvider.getClaimsFromToken(token);
                chain.doFilter(request, httpResponse);
            }
        } catch (ServletException | IOException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            log.error("AuthenticationFilter doFilter error: {}", e.getMessage());
        }
    }

}
