package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.configuration.UserConfiguration;
import io.metadew.iesi.metadata.definition.User;
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

        User users = userConfiguration.getUser(name);
        if (users.getName().isEmpty()) {
            throw new UsernameNotFoundException("BAD CREDENTIALS");
        }
        return new CustomUserDetails(users);

    }
}
