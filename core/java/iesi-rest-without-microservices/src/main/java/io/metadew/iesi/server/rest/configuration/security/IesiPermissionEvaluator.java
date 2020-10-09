package io.metadew.iesi.server.rest.configuration.security;

import io.metadew.iesi.metadata.definition.SecuredObject;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

// https://www.baeldung.com/spring-security-create-new-custom-security-expression
public class IesiPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || (targetDomainObject == null) || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(authentication, targetDomainObject, (String) permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new UnsupportedOperationException("cannot authorize using this manner");
    }

    private boolean hasPrivilege(Authentication authentication, Object securedObject, String permission) {
        if (securedObject instanceof SecuredObject) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals(((SecuredObject) securedObject).getSecurityGroupName() + "_" + permission));
        } else {
            return true;
        }
    }
}
