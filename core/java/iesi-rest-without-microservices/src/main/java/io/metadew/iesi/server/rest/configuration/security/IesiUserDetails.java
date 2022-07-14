package io.metadew.iesi.server.rest.configuration.security;

import io.metadew.iesi.metadata.definition.user.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Data
public class IesiUserDetails implements UserDetails {
    private final User user;
    private final Collection<? extends IESIGrantedAuthority> grantedAuthorities;

    public IesiUserDetails(User user, Collection<? extends IESIGrantedAuthority> grantedAuthorities) {
        this.user = user;
        this.grantedAuthorities = grantedAuthorities;
    }

    public UUID getId() {
        return user.getMetadataKey().getUuid();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !user.isExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !user.isCredentialsExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

}