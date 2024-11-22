package org.example.web_mng_authentication.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web_mng_authentication.user.dto.UserLoginRequestDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final UserApiService userApiService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        log.info("login success");

        // 사용자가 입력한 인증정보 받기, POST method 값이기 때문에 input stream으로 받았다.
//        UserLoginRequestDto creds;
//
//        try {
//            creds = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestDto.class);
//            log.info("login user : {}", creds.getUserId());
//        } catch (IOException e) {
//            log.error(e.getLocalizedMessage());
//            throw new RuntimeException(e);
//        }
//        userApiService.loginCallback(creds.getUserId(), true, "");
        log.info("============onAuthenticationSuccess============");
    }

}
