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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
//        String userPw = userDetails.getPassword();
//        Optional<UserInfo> user = userRepository.findByUserId(userDetails.getUsername().toString());
        String userId = authentication.getName();

        try {
            UserResponseAllDto user = userApiService.findByUserId(userId);
//            System.out.println("user login info id : "+ userDetails.getUsername() + ", pw : " + userPw);
            String jwtToken = tokenProvider.createAccessToken(authentication.getName(), user.getUserName(), user.getEmail());
            if(!userId.isEmpty()) {
                // 헤더에 토큰을 저장하여 게이트웨이 측으로 전달하려고 했으나 response.sendRedirect() 는 HTTP 헤더를 클라이언트에 직접 전달하지 않고, 리다이렉트가 클라이언트로 새로운 요청을 만들도록 지시한다.. 라고 한다.
                // 토큰이 너무 직접 적으로 노출 되기 때문에 보안상 좋지 않다 getWriter를 사용해서 json 타입으로 반환하는 것은?

//                // URl 형식으로 리턴
//                String redirectUrl = "http://localhost:8000/service2/request?token=" + URLEncoder.encode(jwtToken, StandardCharsets.UTF_8);
//                response.sendRedirect(redirectUrl);
//
//                log.info("Login successful. Redirected with token in URL : {}", redirectUrl);

                log.info("userId : {}, user.getUserId : {}", userId, user.getUserId());
//                String gatewayUrl = "http://localhost:8000/token";
//                RestTemplate restTemplate = new RestTemplate();
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_JSON);
//                Map<String, String> tokenPayload = Map.of("accessToken", jwtToken);
//
//                HttpEntity<Map<String, String>> entity = new HttpEntity<>(tokenPayload, headers);
//                restTemplate.postForEntity(gatewayUrl, entity, Void.class);

//                // Json 응답 추가 _ 헤더 안될 수도 있으니까...
                response.setContentType("application/json");
                response.getWriter().write("{\"accessToken\": \"" + jwtToken + "\", \"message\": \"Login successful\"}");
//                response.sendRedirect("/loginSuccess");
                response.getWriter().flush();

//                  userApiService.loginCallback(userDetails.getUsername(), true, "");
//                  tokenProvider.createTokenAndAddHeader(response, authentication);
//                  log.info("get access token in header : {}", response.getHeader("access-token"));

                  // 토큰 객체를 리턴해주는 서비스를 호출한다면? 그리고 그 서비스를 호출하는 컨트롤러가 있다면?

            } else {
                log.info("login failed");
                response.sendRedirect("/login");
            }
        } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }
}




