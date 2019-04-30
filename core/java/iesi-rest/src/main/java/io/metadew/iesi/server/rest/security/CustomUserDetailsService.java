package io.metadew.iesi.server.rest.security;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import io.metadew.iesi.server.rest.models.User;
import io.metadew.iesi.server.rest.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

//    @Autowired
//    UserRepository userRepository;
//
//    @Override
//    @Transactional
//    public UserDetails loadUserByUsername(String usernameOrEmail)
//            throws UsernameNotFoundException {
//        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
//                .orElseThrow(() ->
//                        new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail)
//        );
//
//        return UserPrincipal.create(user);
//    }
//
//    @Transactional
//    public UserDetails loadUserById(Long id) {
//        User user = userRepository.findById(id).orElseThrow(
//            () -> new ResourceNotFoundException("User", "id", id)
//        );
//
//        return UserPrincipal.create(user);
//    }

    @Autowired
    private WebApplicationContext applicationContext;
    private UserRepository userRepository;

    public CustomUserDetailsService() {
        super();
    }

    @PostConstruct
    public void completeSetup() {
        userRepository = applicationContext.getBean(UserRepository.class);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        final User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserPrincipal(user);
    }
}
