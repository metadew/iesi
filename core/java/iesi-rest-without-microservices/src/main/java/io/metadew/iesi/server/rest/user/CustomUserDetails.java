package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.definition.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getActive().equalsIgnoreCase("y");
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.getLocked().equalsIgnoreCase("y");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !user.getExpired().equalsIgnoreCase("y");
    }

    @Override
    public boolean isEnabled() {
        return user.getActive().equalsIgnoreCase("y");
    }

}