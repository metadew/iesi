package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Profile("security")
public class JWTAuthenticationConverter implements AuthenticationConverter {

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
        Algorithm algorithm = Algorithm.HMAC256("secret");
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("iesi")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return new UsernamePasswordAuthenticationToken(jwt.getSubject(), null, jwt.getClaim("authorities").asList(String.class).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
    }

}
