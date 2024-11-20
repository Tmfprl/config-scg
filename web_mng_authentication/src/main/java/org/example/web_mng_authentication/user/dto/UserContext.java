package org.example.web_mng_authentication.user.dto;

import org.example.web_mng_authentication.domain.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserContext extends User {
    private final UserInfo userInfo;

    public UserContext(UserInfo userInfo, Collection<? extends GrantedAuthority> authorities) {
        super(userInfo.getUserId(), userInfo.getUserPassword(), authorities);
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
