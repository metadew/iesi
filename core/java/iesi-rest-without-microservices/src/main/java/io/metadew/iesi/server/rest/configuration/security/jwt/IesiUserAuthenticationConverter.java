package io.metadew.iesi.server.rest.configuration.security.jwt;

import io.metadew.iesi.server.rest.configuration.security.IesiUserDetailsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@ConditionalOnWebApplication
public class IesiUserAuthenticationConverter implements UserAuthenticationConverter {

    private final IesiUserDetailsManager iesiUserDetailsManager;

    @Autowired
    public IesiUserAuthenticationConverter(IesiUserDetailsManager iesiUserDetailsManager) {
        this.iesiUserDetailsManager = iesiUserDetailsManager;
    }

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap();
        response.put("user_name", authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put("authorities", AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }

        return response;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        return new UsernamePasswordAuthenticationToken(map.get("user_name"), null, iesiUserDetailsManager.getGrantedAuthorities((String) map.get("user_name")));
    }
}
