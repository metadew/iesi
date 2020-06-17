package io.metadew.iesi.server.rest.configuration.security.jwt;

import org.springframework.context.annotation.Profile;
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

    public JWTAuthenticationFilter(JWTAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = jwtAuthenticationConverter.convert(httpServletRequest);
        if (authentication == null) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

}
