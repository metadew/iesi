package io.metadew.iesi.server.rest.configuration.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Profile("security")
public class JWTAuthenticationConverter implements AuthenticationConverter {

    private final JwtService jwtService;

    @Autowired
    public JWTAuthenticationConverter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    private static final String AUTHENTICATION_SCHEME = "Bearer ";

    @Override
    public Authentication convert(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        if (header == null) {
            return null;
        }

        header = header.trim();
        if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME)) {
            return null;
        }

        String token = header.substring(7);
        return jwtService.generateUsernamePasswordAuthenticationToken(token);
    }

}
