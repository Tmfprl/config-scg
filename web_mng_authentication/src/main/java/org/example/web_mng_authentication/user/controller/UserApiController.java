package org.example.web_mng_authentication.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web_mng_authentication.exception.ServiceCoustomException;
import org.example.web_mng_authentication.exception.response.ErrorCode;
import org.example.web_mng_authentication.jwt.TokenProvider;
import org.example.web_mng_authentication.user.dto.UserInfoDto;
import org.example.web_mng_authentication.user.dto.UserResponseDto;
import org.example.web_mng_authentication.user.service.UserApiService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor // final이 선언된 모든 필드를 인자값으로 하는 생성자를 대신 생성하여, 빈을 생성자로 주입받게 한다.
@RestController
public class UserApiController {

    private final UserApiService userApiService;
    private final TokenProvider tokenProvider;

    @PostMapping("/user/saveUser")
    public ResponseEntity<Long> saveUser(@RequestBody UserInfoDto userInfoDto) throws Exception {
        log.info("save user information {}", userInfoDto.toString());

        String token = tokenProvider.createAccessToken(userInfoDto.getUserId(), userInfoDto.getUserName(), userInfoDto.getEmail());
        log.info("created token {}", token);
        return ResponseEntity.ok()
                .body(userApiService.signIn(userInfoDto));
    }

    @GetMapping("/getUser/{userId}")
    public ResponseEntity<Page<UserResponseDto>> getUser(@PathVariable String userId,
                                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info(String.valueOf(request.getHeaderNames()));
        log.info("Authorization ::::::: {}", request.getHeader("Authorization"));

        if(userApiService.findByUserId(userId) == null){
            throw new ServiceCoustomException(ErrorCode.USER_NOT_FOUND, "NOT FOUND USER");
        }
//        userDetails = userApiService.loadUserByUsername(userId);
        log.info("get user information {}", userId);
        Pageable pageable = PageRequest.of(0, 50, Sort.by("userId"));
        return ResponseEntity.ok()
                .body(userApiService.findById(userId, pageable));
    }

    @GetMapping("/login/getUser/{userId}/{userName}/{userEmail}")
    public void createToken(@PathVariable("userId") String userId,
                                              @PathVariable("userName") String userName,
                                              @PathVariable("userEmail") String userEmail) throws Exception {
        log.info("createToken userId : {}", userId);
        log.info("createToken userName : {}", userName);
        log.info("createToken userEmail : {}", userEmail);
        String jwtToken = tokenProvider.createAccessToken(userId, userName, userEmail);
        log.info("createToken userId : {}", jwtToken);
    }

    @GetMapping("/loginSuccess")
    public ResponseEntity<String> loginSuccess(@RequestBody String accessToken) throws Exception {
        log.info("get token {}", accessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(accessToken);
    }

}
