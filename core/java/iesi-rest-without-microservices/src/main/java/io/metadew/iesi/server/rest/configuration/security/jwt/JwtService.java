package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import io.metadew.iesi.server.rest.user.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@Profile("security")
public class JwtService {

    private static final String ISSUER = "iesi";
    private static final String AUTHORITIES_CLAIM = "authorities";

    @Value("${iesi.security.jwt.secret}")
    private String secret;

    @Value("${iesi.security.jwt.expiry-date}")
    private Long accessTokenExpiryDate;

    private DecodedJWT verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(e.toString());
        }
    }

    public UsernamePasswordAuthenticationToken generateUsernamePasswordAuthenticationToken(String token) {
        DecodedJWT jwt = verify(token);
        return new UsernamePasswordAuthenticationToken(jwt.getSubject(), null, jwt.getClaim("authorities").asList(String.class).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
    }

    public AuthenticationResponse generateAuthenticationResponse(Authentication authentication) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plus(accessTokenExpiryDate, ChronoUnit.SECONDS);
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(authentication.getName())
                .withIssuedAt(Timestamp.valueOf(now))
                .withExpiresAt(Timestamp.valueOf(expiresAt))
                .withArrayClaim(AUTHORITIES_CLAIM, authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray(String[]::new))
                .sign(algorithm);
        return new AuthenticationResponse(token, ChronoUnit.SECONDS.between(now, expiresAt));
    }
}

