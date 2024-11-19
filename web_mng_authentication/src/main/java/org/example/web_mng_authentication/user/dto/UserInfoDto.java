package org.example.web_mng_authentication.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.web_mng_authentication.domain.UserInfo;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
@ToString
public class UserInfoDto {
    private String userId;
    private String userName;
    private String password;
    private String email;
    private String userStateCode;

    @Builder
    public UserInfoDto(String userId, String userName, String password, String email, String userStateCode) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.userStateCode = userStateCode;
    }

    public UserInfo toEntity(PasswordEncoder encoder) throws Exception {
        return UserInfo.builder()
                .userName(userName)
                .userId(userId)
                .email(email)
                .userPassword(encoder.encode(password)) // 패스워드 인코딩
                .userStateCode(userStateCode)
                .build();
    }
}
