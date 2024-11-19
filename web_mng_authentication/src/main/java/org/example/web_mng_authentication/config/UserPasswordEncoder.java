package org.example.web_mng_authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

@Configuration
public class UserPasswordEncoder {
    // 비밀번호 암호화
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
