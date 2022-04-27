package io.metadew.iesi.server.rest.configuration.security;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
        //log.info("FRAMEWORK CRYPTO : " + FrameworkCrypto.getInstance());
        //log.info("CLIENT_ID REQUEST : " + request.getParameter("client_id") + " | " + frameworkCrypto.encrypt(request.getParameter("client_id")));
        //log.info("CLIENT_SECRET REQUEST : " + request.getParameter("client_secret")  + " | " + frameworkCrypto.encrypt(request.getParameter("client_secret")));
        CorsConfiguration corsConfiguration = this.configSource.getCorsConfiguration(request);
        boolean isValid = this.processor.processRequest(corsConfiguration, request, response);
        if (isValid && !CorsUtils.isPreFlightRequest(request)) {
            filterChain.doFilter(request, response);
        }
    }


}
