package org.example.web_mng_authentication.config;

import lombok.RequiredArgsConstructor;
import org.example.web_mng_authentication.jwt.JwtTokenFilter;
import org.example.web_mng_authentication.jwt.TokenProvider;
import org.example.web_mng_authentication.user.service.CustomAuthSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
// Spring Security 설정들을 활성화시켜 준다
@EnableWebSecurity (debug = true) // request가 올 떄마다 어떤 filter를 사용하고 있는지 출력을 해준다.
@EnableMethodSecurity
public class SecurityConfig {
    private final TokenProvider tokenProvider;

    @Value("${token.secret}")
    private String TOKEN_SECRET;

    // 토큰 인증 필터가 추가 되어야한다.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthSuccessHandler customAuthSuccessHandler) throws Exception {
        JwtTokenFilter tokenFilter = new JwtTokenFilter(tokenProvider);
        http
                // CSRF 보호를 비활성화합니다. CSRF 보호는 주로 브라우저 클라이언트를 대상으로 하는 공격을 방지하기 위해 사용됩니다.
                // 사이트 간 요청 위조라는 공격 방식으로 사용자가 의도하지 않았지만 공격자의 의도에 따라 자신도 모르게 서버를 공격하게 되는걸 CSRF 라고 한다.
                // 쿠키를 통한 인증방식을 사용하지 않는다면 CSRF를 방어하지 않아도 좋다
                .csrf(csrf -> csrf.disable())
                // 요청에 대한 접근 권한을 설정합니다.
                .authorizeRequests(authorize -> authorize
                        // /auth/signIn 경로에 대한 접근을 허용합니다. 이 경로는 인증 없이 접근할 수 있습니다.
                        .requestMatchers("/login", "/user/**", "/error").permitAll()  // 모든접근허용
                        .anyRequest().authenticated()   // 제한된 접근, 그 외의 모든 요청은 인증이 필요합니다.
                )

//                .exceptionHandling((exceptionConfig) ->
//                exceptionConfig
//                        .authenticationEntryPoint(unauthorizedEntryPoint)
//                        .accessDeniedHandler(accessDeniedHandler)
//        ) // 401 403 관련 예외처리_나중에 예외처리할때 한번에 설정
                .formLogin(
                        (formLogin) -> formLogin
                                .successHandler(customAuthSuccessHandler)
                )
                .logout(Customizer.withDefaults())
//                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class) // 만약 게이트웨이에서 토큰 인증을 할 수 있다면 필요없다.
                // 세션 관리 정책을 정의합니다. 여기서는 세션을 사용하지 않도록 STATELESS로 설정합니다. (세션이나 쿠키를 사용한 인증이 아닌 토큰을 사용한 인증이기 때문에)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        // 설정된 보안 필터 체인을 반환합니다.
        return http.build();
    }


}

