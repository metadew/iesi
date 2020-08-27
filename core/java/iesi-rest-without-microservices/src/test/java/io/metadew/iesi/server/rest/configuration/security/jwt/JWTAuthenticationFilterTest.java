package io.metadew.iesi.server.rest.configuration.security.jwt;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@PrepareForTest(JWTAuthenticationFilter.class)
@ActiveProfiles({"test", "security"})
public class JWTAuthenticationFilterTest {

       @InjectMocks
    private JWTAuthenticationConverter jwtAuthenticationConverter;
    @Mock
    private  JwtService jwtService;

    private static final String token = "";
    private static final String testUri = "/scripts";
    @Test
    public void testDoFilter() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.addHeader("TOKEN", token);
//        request.setRequestURI(testUri);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        jwtAuthenticationConverter = new JWTAuthenticationConverter(jwtService);
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(jwtAuthenticationConverter);

        jwtAuthenticationFilter.doFilterInternal(request, response,
                filterChain);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
