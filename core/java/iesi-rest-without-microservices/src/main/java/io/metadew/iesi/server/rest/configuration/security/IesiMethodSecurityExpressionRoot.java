package io.metadew.iesi.server.rest.configuration.security;

import io.metadew.iesi.metadata.definition.SecuredObject;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.stream.Collectors;

@Log4j2
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
                        .anyMatch(authority -> authority.equals(permission + "@" + ((SecuredObject<? extends MetadataKey>) securedObject).getSecurityGroupName()));
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