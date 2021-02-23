package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;

@Component
// @Profile("security")
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTAuthenticationConverter jwtAuthenticationConverter;
    private final Clock clock;

    @Value("${iesi.security.enabled:false}")
    private boolean enableSecurity;

    @Autowired
    public JWTAuthenticationFilter(JWTAuthenticationConverter jwtAuthenticationConverter, Clock clock) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
        this.clock = clock;
    }

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (enableSecurity) {
            try {
                Authentication authentication = jwtAuthenticationConverter.convert(httpServletRequest);
                if (authentication == null) {
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                    return;
                }
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } catch (JWTVerificationException e) {
                setErrorResponse(HttpStatus.UNAUTHORIZED, httpServletResponse, e);
            }
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        ApiError apiError = new ApiError(status, ex, LocalDateTime.now(clock));
        String json = apiError.convertToJson();
        response.getWriter().write(json);
    }
}
