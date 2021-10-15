package io.metadew.iesi.server.rest.configuration.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.DenyAllPermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@ConditionalOnWebApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Log4j2
// https://www.baeldung.com/spring-security-create-new-custom-security-expression
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
    @Value("${iesi.security.enabled:false}")
    private boolean enableSecurity;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        log.info("setting custom IESI security expressions");
        IesiMethodSecurityExpressionHandler methodSecurityExpressionHandler = new IesiMethodSecurityExpressionHandler(enableSecurity);
        methodSecurityExpressionHandler.setPermissionEvaluator(new DenyAllPermissionEvaluator());
        return methodSecurityExpressionHandler;
    }
}