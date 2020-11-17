package io.metadew.iesi.server.rest.configuration.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

// https://www.baeldung.com/spring-security-create-new-custom-security-expression
public class IesiMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    private final boolean securityEnabled;
    private final AuthenticationTrustResolver trustResolver;

    public IesiMethodSecurityExpressionHandler(boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
        this.trustResolver = new AuthenticationTrustResolverImpl();
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        IesiMethodSecurityExpressionRoot root = new IesiMethodSecurityExpressionRoot(authentication, securityEnabled);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }
}