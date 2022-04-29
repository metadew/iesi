package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.configuration.security.IesiUserDetailsManager;
import io.metadew.iesi.server.rest.configuration.security.MethodSecurityConfiguration;
import io.metadew.iesi.server.rest.user.AuthenticationResponse;
import io.metadew.iesi.server.rest.user.IUserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest(classes = {Application.class, MethodSecurityConfiguration.class, TestConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true", "iesi.security.enabled=true",
                "iesi.security.jwt.secret=secret", "iesi.security.jwt.expiry-date=10"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ActiveProfiles({"test"})
@DirtiesContext
class JwtServiceTest {

    @MockBean
    private IesiUserDetailsManager iesiUserDetailsManager;

    @MockBean
    private IUserService userService;

    @MockBean
    private Clock clock;

//    @Test
//    void generateUsernamePasswordAuthenticationToken() {
//        when(clock.instant()).thenReturn(
//                LocalDateTime.now().toInstant(ZoneOffset.UTC));
//        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
//        String token = JWT.create()
//                .withIssuer("iesi")
//                .withSubject("testUser")
//                .withIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
//                .withExpiresAt(Timestamp.valueOf(LocalDateTime.now().plus(10, ChronoUnit.SECONDS)))
//                .sign(Algorithm.HMAC256("secret"));
//        assertThat(jwtService.generateUsernamePasswordAuthenticationToken(token))
//                .isEqualTo(new UsernamePasswordAuthenticationToken("testUser", null, new HashSet<>()));
//    }
//
//
//    @Test
//    void generateAuthenticationResponse() {
//        when(clock.instant()).thenReturn(
//                LocalDateTime.of(
//                        LocalDate.of(2020, 1, 1),
//                        LocalTime.of(20, 5, 5, 10)).toInstant(ZoneOffset.UTC));
//        when(clock.getZone())
//                .thenReturn(ZoneOffset.UTC);
//        when(userService.get())
//        String token = JWT.create()
//                .withIssuer("iesi")
//                .withSubject("testUser")
//                .withIssuedAt(Timestamp.valueOf(LocalDateTime.of(
//                        LocalDate.of(2020, 1, 1),
//                        LocalTime.of(20, 5, 5, 10))))
//                .withExpiresAt(Timestamp.valueOf(LocalDateTime.of(
//                        LocalDate.of(2020, 1, 1),
//                        LocalTime.of(20, 5, 5, 10)).plus(10, ChronoUnit.SECONDS)))
//                .sign(Algorithm.HMAC256("secret"));
//        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
//        assertThat(jwtService.generateAuthenticationResponse(new UsernamePasswordAuthenticationToken("testUser", null, new HashSet<>())))
//                .isEqualTo(new AuthenticationResponse(token, 10L));
//    }
}

