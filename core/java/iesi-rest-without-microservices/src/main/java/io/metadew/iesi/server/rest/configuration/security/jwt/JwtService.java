package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import io.metadew.iesi.server.rest.configuration.security.IesiUserDetails;
import io.metadew.iesi.server.rest.configuration.security.IesiUserDetailsManager;
import io.metadew.iesi.server.rest.user.AuthenticationResponse;
import io.metadew.iesi.server.rest.user.IUserService;
import io.metadew.iesi.server.rest.user.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@ConditionalOnWebApplication
public class JwtService {

    private static final String ISSUER = "iesi";

    private final Clock clock;

    private final IesiUserDetailsManager iesiUserDetailsManager;

    private final IUserService userService;

    @Value("${iesi.security.jwt.secret}")
    private String secret;

    @Value("${iesi.security.jwt.expiry-date}")
    private Long accessTokenExpiryDate;

    public JwtService(Clock clock, IesiUserDetailsManager iesiUserDetailsManager, IUserService userService) {
        this.clock = clock;
        this.iesiUserDetailsManager = iesiUserDetailsManager;
        this.userService = userService;
    }

    private DecodedJWT verify(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }

    public UsernamePasswordAuthenticationToken generateUsernamePasswordAuthenticationToken(String token) {
        DecodedJWT jwt = verify(token);
        return new UsernamePasswordAuthenticationToken(
                jwt.getSubject(),
                null,
                iesiUserDetailsManager.getGrantedAuthorities(jwt.getSubject()));
    }

    public AuthenticationResponse generateAuthenticationResponse(Authentication authentication) {
        System.out.println("IN JWT AUTH :" + authentication);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime expiresAt = now.plus(accessTokenExpiryDate, ChronoUnit.SECONDS);
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(authentication.getName())
                .withIssuedAt(Timestamp.valueOf(now))
                .withExpiresAt(Timestamp.valueOf(expiresAt))
                .withClaim("uuid", ((IesiUserDetails) authentication.getPrincipal()).getId().toString())
                .sign(algorithm);
        UserDto userDto = userService.get(((IesiUserDetails) authentication.getPrincipal()).getId())
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Cannot find user %s (%s)",
                                ((IesiUserDetails) authentication.getPrincipal()).getId().toString(),
                                ((IesiUserDetails) authentication.getPrincipal()).getUsername())));
        return new AuthenticationResponse(token, ChronoUnit.SECONDS.between(now, expiresAt), userDto.getRoles());
    }
}

