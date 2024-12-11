package org.example.web_mng_authentication.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web_mng_authentication.jwt.TokenProvider;
import org.example.web_mng_authentication.user.dto.UserResponseAllDto;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserApiService userApiService;
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetails userDetails = userApiService.loadUserByUsername(authentication.getName().toString());
        String userId = authentication.getName();

        try {
            UserResponseAllDto user = userApiService.findByUserId(userId);
            String jwtToken = tokenProvider.createAccessToken(authentication.getName(), user.getUserName(), user.getEmail());
            if(!userId.isEmpty()) {

//                // URl 형식으로 리턴
//                String redirectUrl = "http://localhost:8000/service2/request?token=" + URLEncoder.encode(jwtToken, StandardCharsets.UTF_8);
//                response.sendRedirect(redirectUrl);
//                log.info("Login successful. Redirected with token in URL : {}", redirectUrl);

                log.info("userId : {}, user.getUserId : {}", userId, user.getUserId());
//                String gatewayUrl = "http://localhost:8000/token";

//                headers.setContentType(MediaType.APPLICATION_JSON);

//                // Json 응답 추가 _ 헤더 안될 수도 있으니까...
                response.addHeader("token", jwtToken);
                response.setHeader(HttpHeaders.AUTHORIZATION, jwtToken);
                response.setContentType("application/json");
                response.getWriter().write("{\"accessToken\": \"" + jwtToken + "\", \"message\": \"Login successful\"}");

            } else {
                log.info("login failed");
                response.sendRedirect("/login");
            }
        } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }
}