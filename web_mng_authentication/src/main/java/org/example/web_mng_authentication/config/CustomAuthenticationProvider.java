package org.example.web_mng_authentication.config;

import jakarta.annotation.Resource;
import org.example.web_mng_authentication.user.dto.UserContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private PasswordEncoder passwordEncoder;

    //    Authentication 객체로부터 인증에 필요한 정보(username, password 등)를 받아오고, userDetailsService 인터페이스를
//    구현한 객체(CustomUserDetailsService)로 부터 DB에 저장된 유저 정보를 받아온 후, password를 비교하고 인증이 완료되면 인증이 완료된 Authentication 객체를 리턴
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserContext userContext = (UserContext) userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userContext.getUserInfo().getUserPassword())) {
            throw new BadCredentialsException("BadCredentialsException");
        }

        //UsernamePasswordAuthenticationToken은 Authentication 인터페이스를 구현한 인증 객체
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
