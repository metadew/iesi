package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.configuration.UserConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserConfiguration userConfiguration;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        return userConfiguration
                .getUser(name)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
