package io.metadew.iesi.server.rest.configuration.security;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.server.rest.configuration.IesiHttpServletRequestWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;
import org.springframework.web.cors.*;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class IesiCorsFilter extends OncePerRequestFilter {

    private final CorsConfigurationSource configSource;
    private CorsProcessor processor = new DefaultCorsProcessor();
    private FrameworkCrypto frameworkCrypto = FrameworkCrypto.getInstance();

    public IesiCorsFilter(CorsConfigurationSource configSource) {
        Assert.notNull(configSource, "CorsConfigurationSource must not be null");
        this.configSource = configSource;
    }

    public void setCorsProcessor(CorsProcessor processor) {
        Assert.notNull(processor, "CorsProcessor must not be null");
        this.processor = processor;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        IesiHttpServletRequestWrapper iesiHttpServletRequestWrapper = new IesiHttpServletRequestWrapper(request);
        if (request.getRequestURI().equals("/api/oauth/token")) {
            iesiHttpServletRequestWrapper.addParameter("client_id", frameworkCrypto.decryptIfNeeded(request.getParameter("client_id")));
            iesiHttpServletRequestWrapper.addParameter("client_secret", frameworkCrypto.decryptIfNeeded(request.getParameter("client_secret")));
            if (request.getParameter("grant_type").equals("password")) {
                iesiHttpServletRequestWrapper.addParameter("grant_type", "password");
                iesiHttpServletRequestWrapper.addParameter("username", request.getParameter("username"));
                iesiHttpServletRequestWrapper.addParameter("password", request.getParameter("password"));
            } else if (request.getParameter("grant_type").equals("refresh_token")) {
                iesiHttpServletRequestWrapper.addParameter("grant_type", "refresh_token");
                iesiHttpServletRequestWrapper.addParameter("refresh_token", request.getParameter("refresh_token"));
            }

        }

        CorsConfiguration corsConfiguration = this.configSource.getCorsConfiguration(iesiHttpServletRequestWrapper);
        boolean isValid = this.processor.processRequest(corsConfiguration, iesiHttpServletRequestWrapper, response);
        if (isValid && !CorsUtils.isPreFlightRequest(iesiHttpServletRequestWrapper)) {
            filterChain.doFilter(iesiHttpServletRequestWrapper, response);
        }
    }


}
