package org.example.web_mng_authentication.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web_mng_authentication.config.TokenProvider;
import org.example.web_mng_authentication.user.dto.UserInfoDto;
import org.example.web_mng_authentication.user.dto.UserResponseDto;
import org.example.web_mng_authentication.user.service.UserApiService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor // final이 선언된 모든 필드를 인자값으로 하는 생성자를 대신 생성하여, 빈을 생성자로 주입받게 한다.
@RestController
public class UserApiController {

    private final UserApiService userApiService;
    private final TokenProvider tokenProvider;

    @PostMapping("/user/controller/saveUser")
    public ResponseEntity<Long> saveUser(@RequestBody UserInfoDto userInfoDto) throws Exception {
        log.info("save user information {}", userInfoDto.toString());
        return ResponseEntity.ok()
                .body(userApiService.singIn(userInfoDto));
    }

    @GetMapping("/user/controller/getUser/{userId}")
    public ResponseEntity<Page<UserResponseDto>> getUser(@PathVariable("userId") String userId) throws Exception {
        log.info("get user information {}", userId);
        Pageable pageable = PageRequest.of(0, 50, Sort.by("userId"));
        return ResponseEntity.ok()
                .body(userApiService.findById(userId, pageable));
    }

    @GetMapping("/user/controller/getUser/{userId}/{userName}/{userEmail}")
    public void createToken(@PathVariable("userId") String userId,
                                              @PathVariable("userName") String userName,
                                              @PathVariable("userEmail") String userEmail) throws Exception {
        log.info("createToken userId : {}", userId);
        log.info("createToken userName : {}", userName);
        log.info("createToken userEmail : {}", userEmail);
        String jwtToken = tokenProvider.createAccessToken(userId, userName, userEmail);
        log.info("createToken userId : {}", jwtToken);
    }

}