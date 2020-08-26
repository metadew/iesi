package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import io.metadew.iesi.server.rest.user.CustomUserDetailsManager;
import io.metadew.iesi.server.rest.user.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {UserController.class, JwtService.class, CustomUserDetailsManager.class, AuthenticationManager.class})

public class JwtServiceTest {
    @MockBean
    private JwtService jwtService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private CustomUserDetailsManager userDetailsManager;
    @MockBean
    private UserController userController;
    @Autowired
    private MockMvc mvc;

    private DecodedJWT verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(e.toString());
        }
    }

    private DecodedJWT verifyInvalid(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("invalid")
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(e.toString());
        }
    }

    private DecodedJWT verifyDate(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm).acceptExpiresAt(5 * 60).build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(e.toString());
        }
    }

    @Test
    public void shouldThrowSignatureVerificationException() {

        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCJ9.AbIJTDMFc75MhvcP03nJPyCPzZtQcGEp-zWfOkEE";
        assertThatThrownBy(() -> {
            verify(token);
        }).isInstanceOf(JWTVerificationException.class).hasMessageContaining("com.auth0.jwt.exceptions.SignatureVerificationException: The Token's Signature resulted invalid when verified using the Algorithm: HmacSHA256");
    }

    @Test
    public void shouldThrowOnInvalidIssuer() {
        String token = "eyJhbGciOiJIUzI1NiIsImN0eSI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCJ9.mZ0m_N1J4PgeqWmi903JuUoDRZDBPB7HwkS4nVyWH1M";
        assertThatThrownBy(() -> {
            verifyInvalid(token);
        }).isInstanceOf(JWTVerificationException.class).hasMessageContaining("com.auth0.jwt.exceptions.InvalidClaimException: The Claim 'iss' value doesn't match the required issuer.");
    }

    @Test
    public void shouldThrowOnInvalidExpiresAtIfPresent() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE0Nzc1OTJ9.isvT0Pqx0yjnZk53mUFSeYFJLDs-Ls9IsNAm86gIdZo";
        assertThatThrownBy(() -> {
            verifyDate(token);
        }).isInstanceOf(JWTVerificationException.class).hasMessageContaining("com.auth0.jwt.exceptions.TokenExpiredException: The Token has expired on Sun Jan 18 03:26:32 CET 1970.");
    }
}
