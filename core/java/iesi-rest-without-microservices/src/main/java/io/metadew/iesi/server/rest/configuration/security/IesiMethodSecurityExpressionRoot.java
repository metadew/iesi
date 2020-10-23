package io.metadew.iesi.server.rest.configuration.security;

import io.metadew.iesi.metadata.definition.SecuredObject;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

// https://www.baeldung.com/spring-security-create-new-custom-security-expression
public class IesiMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private final boolean securityEnabled;
    private Object filterObject;
    private Object returnObject;
    private Object target;

    public IesiMethodSecurityExpressionRoot(Authentication authentication, boolean securityEnabled) {
        super(authentication);
        this.securityEnabled = securityEnabled;
    }

    public boolean hasPrivilege(String privilege) {
        return !securityEnabled || getAuthentication().getAuthorities().stream()
                .filter(authority -> authority instanceof IESIGrantedAuthority)
                .map(authority -> (IESIGrantedAuthority) authority)
                .map(IESIGrantedAuthority::getPrivilegeName)
                .anyMatch(s -> s.equals(privilege));
    }

    public boolean isMember(String securityGroup) {
        return !securityEnabled || getAuthentication().getAuthorities().stream()
                .filter(authority -> authority instanceof IESIGrantedAuthority)
                .map(authority -> (IESIGrantedAuthority) authority)
                .map(IESIGrantedAuthority::getSecurityGroupName)
                .anyMatch(s -> s.equals(securityGroup));
    }

    public boolean hasPrivilege(Authentication authentication, Object securedObject, String permission) {
        if (securityEnabled) {
            if (securedObject instanceof SecuredObject) {
                return authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(authority -> authority.equals(((SecuredObject) securedObject).getSecurityGroupName() + "_" + permission));
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    public Object getFilterObject() {
        return filterObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public Object getReturnObject() {
        return returnObject;
    }

    /**
     * Sets the "this" property for use in expressions. Typically this will be the "this"
     * property of the {@code JoinPoint} representing the method invocation which is being
     * protected.
     *
     * @param target the target object on which the method in is being invoked.
     */
    void setThis(Object target) {
        this.target = target;
    }

    public Object getThis() {
        return target;
    }
}