package io.metadew.iesi.server.rest.configuration.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(JWTAuthenticationFilter.class)
@ContextConfiguration(classes = {JWTAuthenticationFilter.class, JWTAuthenticationConverter.class, JwtService.class})
@ActiveProfiles({"test", "security"})

public class JWTAuthenticationFilterTest {

    @Autowired
    private JWTAuthenticationConverter jwtAuthenticationConverter;

    @Test
    public void testJWTDecodeException() throws Exception {

        String token = "eyJ0eXAiOJKV1QiLCUzI1NiJ9.eyJzdWIiOiJMyIiwiaXNzIjoiaWVzaSIsImV4cCI6MTU5ODU2ODM2MywiaWF0IjoxNTk4NDY4MzYzLCJhdXRob3JpdGllcyI6W119.alYmNwRyiqS8VijXdQ2F0ooVdr5KkITz2bycffG6_fI";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/scripts");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(jwtAuthenticationConverter);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthenticationFilter.doFilterInternal(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("com.auth0.jwt.exceptions.JWTDecodeException: The string");
    }

    @Test
    public void testInvalidClaims() throws Exception {

        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE0Nzc1OTJ9.isvT0Pqx0yjnZk53mUFSeYFJLDs-Ls9IsNAm86gIdZo";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/scripts");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(jwtAuthenticationConverter);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthenticationFilter.doFilterInternal(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("com.auth0.jwt.exceptions.InvalidClaimException: The Claim");
    }

    @Test
    public void testSignatureVerificationException() throws Exception {

        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCJ9.AbIJTDMFc75MhvcP03nJPyCPzZtQcGEp-zWfOkEE";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/scripts");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(jwtAuthenticationConverter);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthenticationFilter.doFilterInternal(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("com.auth0.jwt.exceptions.SignatureVerificationException: The Token's Signature resulted invalid when verified using the Algorithm: HmacSHA256");
    }

    @Test
    public void testExpiredToken() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpZXNpIiwiZXhwIjoxNTk4NjA2NTY4fQ.F7GnAfcklY1-0sG9K4tkz3mfVQ40aAtBG7MUdSI79FI";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/scripts");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(jwtAuthenticationConverter);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthenticationFilter.doFilterInternal(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("com.auth0.jwt.exceptions.TokenExpiredException: The Token has expired ");
    }
}
