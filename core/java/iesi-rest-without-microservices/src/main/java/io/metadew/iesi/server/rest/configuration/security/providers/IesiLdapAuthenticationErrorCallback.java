package io.metadew.iesi.server.rest.configuration.security.providers;

import lombok.extern.log4j.Log4j2;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AuthenticationErrorCallback;
import org.springframework.security.authentication.BadCredentialsException;

@Log4j2
public class IesiLdapAuthenticationErrorCallback implements AuthenticationErrorCallback {
    @Override
    public void execute(Exception e) {
        log.warn(String.format("Cannot authenticate LDAP user: %S", ((NamingException) e).getExplanation()));
        if (e.getMessage().contains("error code 49 - INVALID_CREDENTIALS")) {
            throw new BadCredentialsException("Bad credentials");
        }
    }
}
