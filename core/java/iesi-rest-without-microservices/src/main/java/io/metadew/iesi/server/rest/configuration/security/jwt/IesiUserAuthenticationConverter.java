package io.metadew.iesi.server.rest.configuration.security.jwt;

import io.metadew.iesi.server.rest.configuration.security.IesiUserDetailsManager;
import io.metadew.iesi.server.rest.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@ConditionalOnWebApplication
public class IesiUserAuthenticationConverter implements UserAuthenticationConverter {

    private final IesiUserDetailsManager iesiUserDetailsManager;
    private final UserService userService;


    @Autowired
    public IesiUserAuthenticationConverter(IesiUserDetailsManager iesiUserDetailsManager, @Qualifier("restUserService") UserService userService) {
        this.iesiUserDetailsManager = iesiUserDetailsManager;
        this.userService = userService;
    }

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap();
        response.put(USERNAME, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            HashSet<String> hashSet = new HashSet<>();
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                hashSet.add(authority.getAuthority().split("@")[0]);
            }


            response.put(AUTHORITIES, hashSet);
        }

        return response;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        return new UsernamePasswordAuthenticationToken(map.get(USERNAME), null, iesiUserDetailsManager.getGrantedAuthorities((String) map.get(USERNAME)));
    }
}
