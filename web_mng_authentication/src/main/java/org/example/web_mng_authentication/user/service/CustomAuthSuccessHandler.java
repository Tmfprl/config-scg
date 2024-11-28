package org.example.web_mng_authentication.user.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web_mng_authentication.domain.UserInfo;
import org.example.web_mng_authentication.domain.UserRepository;
import org.example.web_mng_authentication.jwt.TokenProvider;
import org.example.web_mng_authentication.user.dto.UserResponseAllDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserApiService userApiService;
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        UserDetails userDetails = userApiService.loadUserByUsername(authentication.getName().toString());
//        String userPw = userDetails.getPassword();
//        Optional<UserInfo> user = userRepository.findByUserId(userDetails.getUsername().toString());
        String userId = authentication.getName();

        try {
            UserResponseAllDto user = userApiService.findByUserId(userId);
//            System.out.println("user login info id : "+ userDetails.getUsername() + ", pw : " + userPw);
            String jwtToken = tokenProvider.createAccessToken(authentication.getName(), user.getUserName(), user.getEmail());
            if(userId == user.getUserId()) {
                // 헤더에 토큰을 저장하여 게이트웨이 측으로 전달하려고 했으나 response.sendRedirect() 는 HTTP 헤더를 클라이언트에 직접 전달하지 않고, 리다이렉트가 클라이언트로 새로운 요청을 만들도록 지시한다.. 라고 한다.
                // 토큰은 헤더에 넣어도 토큰 자체는 원래 노출되는 것이기 때문에 url 파라미터로 전달하기로 했다.
                String redirectUrl = "http://localhost:8000/service2/request?token=" + URLEncoder.encode(jwtToken, StandardCharsets.UTF_8);
                response.sendRedirect(redirectUrl);

                log.info("Login successful. Redirected with token in URL, URL : {}", redirectUrl);

    //            log.info("login success");
    //            userApiService.loginCallback(userDetails.getUsername(), true, "");
    //            tokenProvider.createTokenAndAddHeader(response, authentication);
//                log.info("get access token in header : {}", response.getHeader("access-token"));
    //            response.sendRedirect("/getUser/" + userDetails.getUsername());

//                log.info("Response Headers: {}", response.getHeaderNames());

            } else {
                log.info("login failed");
                response.sendRedirect("/login");
            }
        } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }
}




