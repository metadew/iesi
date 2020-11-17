package io.metadew.iesi.server.rest.configuration.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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

@Component
@Profile("security")
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTAuthenticationConverter jwtAuthenticationConverter;

    @Autowired
    public JWTAuthenticationFilter(JWTAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
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
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        ApiError apiError = new ApiError(status, ex);
        String json = apiError.convertToJson();
        response.getWriter().write(json);
    }
}
