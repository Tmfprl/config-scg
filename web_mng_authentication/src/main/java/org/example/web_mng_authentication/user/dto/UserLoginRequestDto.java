package org.example.web_mng_authentication.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

@Getter
@NoArgsConstructor
@Repository
public class UserLoginRequestDto {
//    private final PasswordEncoder = passwordEncoder;

    /**
     * 사용자 ID
     */
    private String userId;

    /**
     * 비밀번호
     */
    private String password;

    /**
     * 토큰
     */
    private String token;

    /**
     * 이름
     */
    private String name;

    /**
     * 사용자 로그인 요청 DTO 클래스 생성자
     * 빌더 패턴으로 객체 생성
     *
     * @param userId    이메일
     * @param password 비밀번호
     * @param token    토큰
     */
    @Builder
    public UserLoginRequestDto(String userId, String password, String token, String name) {
        this.userId = userId;
        this.password = password;
        this.token = token;
        this.name = name;
    }

    /**
     * OAuth 로그인 정보 세팅
     *
     * @param userId
     * @param password
     */
    public void setOAuthLoginInfo(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
