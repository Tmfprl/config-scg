package org.example.web_mng_authentication.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name="gyeun_user_info", schema = "srlk")
@ToString
@Getter
@NoArgsConstructor
public class UserInfo {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_no")
    private Long userNo;

    @Column(name = "user_password", unique = true)
    private String userPassword;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_state_code", columnDefinition = "varchar(20) default '00'")
    private String userStateCode;

    @Column(name = "email_addr")
    private String emailAddr;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;


    @Column(name = "login_fall_count", nullable = false, columnDefinition = "smallint default 0")
    private Integer loginFailCount;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "user_id")
    private String userId;

    @Builder
    public UserInfo (Long userNo, String userId, String userName, String email,
                     String userPassword, String userStateCode) {
        this.userNo = userNo;
        this.userId = userId;
        this.userName = userName;
        this.emailAddr = email;
        this.userPassword = userPassword;
        this.userStateCode = userStateCode;
    }

    /**
     * 사용자 refresh token 정보를 필드에 입력한다.
     *
     * @param refreshToken
     * @return
     */
    public UserInfo updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    /**
     * 로그인 실패 시 로그인실패수를 증가시키고 5회 이상 실패한 경우 회원상태를 정지로 변경
     *
     * @return User 사용자 엔티티
     */
    public UserInfo failLogin() {
        this.loginFailCount = loginFailCount + 1;
        if (this.loginFailCount >= 5) {
            this.userStateCode = UserStateCode.HALT.getKey();
        }
        return this;
    }

    /**
     * 로그인 성공 시 로그인실패수와 마지막로그인일시 정보를 갱신
     *
     * @return User 사용자 엔티티
     */
    public UserInfo successLogin() {
        this.loginFailCount = 0;
        this.lastLoginDate = LocalDateTime.now();
        return this;
    }
}
