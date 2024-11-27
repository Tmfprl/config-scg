package org.example.web_mng_authentication.user.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web_mng_authentication.domain.UserInfo;
import org.example.web_mng_authentication.domain.UserRepository;
import org.example.web_mng_authentication.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
        UserDetails userDetails = userApiService.loadUserByUsername(authentication.getName().toString());
        String userPw = userDetails.getPassword();
        Optional<UserInfo> user = userRepository.findByUserId(userDetails.getUsername().toString());

        System.out.println("user login info id : "+ userDetails.getUsername() + ", pw : " + userPw);
        if(user.isPresent()) {
            log.info("login success");
            userApiService.loginCallback(userDetails.getUsername(), true, "");
            tokenProvider.createTokenAndAddHeader(response, authentication);
            log.info("get access token in header : {}", response.getHeader("access-token"));
            response.sendRedirect("/getUser/" + userDetails.getUsername());
        } else {
            log.info("login failed");
        }
    }

}