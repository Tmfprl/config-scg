package org.example.web_mng_authentication.user.service;

import lombok.RequiredArgsConstructor;
import org.example.web_mng_authentication.domain.UserInfo;
import org.example.web_mng_authentication.domain.UserRepository;
import org.example.web_mng_authentication.user.dto.UserContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        UserInfo userInfo = userRepository.findByUser(userId);

        if(userInfo == null)   {
            throw new UsernameNotFoundException(userId + " not found");
        }

        List<GrantedAuthority> role = new ArrayList<>();
        role.add(new SimpleGrantedAuthority("admin"));

        UserContext userContext = new UserContext(userInfo, role);

        return userContext;

    }
}
